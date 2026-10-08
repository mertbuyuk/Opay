package com.opay.service;

import com.opay.model.ProductStock;
import com.opay.model.ReservationStatus;
import com.opay.model.StockReservation;
import com.opay.repository.ProductStockRepository;
import com.opay.repository.StockReservationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import com.opay.events.order.OrderItemEvent;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j//log nesnesi ekler
public class InventoryService {
    private final ProductStockRepository productStockRepository;
    private final StockReservationRepository stockReservationRepository;


    @Transactional
    public ReservationOutcome reserveStock(UUID orderId, List<OrderItemEvent> items){
        if (stockReservationRepository.existsByOrderId(orderId)){
            log.info("Ignoring duplicate OrderCreated for order {} -- reservation already exists", orderId);
            return ReservationOutcome.alreadProcessed();
        }

        List<OrderItemEvent> sortedItems = items.stream()
                .sorted(Comparator.comparing(OrderItemEvent::getSku))
                .toList();

        // Pass 1: check (with locks held for the rest of this transaction).
        for (OrderItemEvent item : sortedItems) {
            ProductStock stock = productStockRepository.findBySkuForUpdate(item.getSku()).orElse(null);
            if (stock == null || stock.getAvailableQuantity() < item.getQuantity()) {
                log.info("Order {} cannot be reserved -- insufficient stock for sku {}", orderId, item.getSku());
                return ReservationOutcome.outOfStock(item.getSku());
            }
        }

        // Pass 2: commit. Re-fetching re-uses the lock this transaction already holds from
        // pass 1 -- Postgres allows a transaction to re-lock rows it already locked.
        for (OrderItemEvent item : sortedItems) {
            ProductStock stock = productStockRepository.findBySkuForUpdate(item.getSku()).orElseThrow();
            stock.setAvailableQuantity(stock.getAvailableQuantity() - item.getQuantity());
            stock.setReservedQuantity(stock.getReservedQuantity() + item.getQuantity());
            productStockRepository.save(stock);

            stockReservationRepository.save(StockReservation.builder()
                    .orderId(orderId)
                    .sku(item.getSku())
                    .quantity(item.getQuantity())
                    .status(ReservationStatus.RESERVED)
                    .build());
        }

        log.info("Reserved stock for order {} ({} line items)", orderId, sortedItems.size());
        return ReservationOutcome.reserved();
    }

    @org.springframework.transaction.annotation.Transactional
    public void releaseStock(UUID orderId) {
        List<StockReservation> reservations =
                stockReservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        if (reservations.isEmpty()) {
            log.info("No RESERVED stock found for order {} -- nothing to release", orderId);
            return;
        }

        for (StockReservation reservation : reservations) {
            ProductStock stock = productStockRepository.findBySkuForUpdate(reservation.getSku())
                    .orElseThrow(() -> new IllegalStateException(
                            "product_stock row missing for sku " + reservation.getSku()
                                    + " while releasing order " + orderId));
            stock.setAvailableQuantity(stock.getAvailableQuantity() + reservation.getQuantity());
            stock.setReservedQuantity(stock.getReservedQuantity() - reservation.getQuantity());
            productStockRepository.save(stock);

            reservation.setStatus(ReservationStatus.RELEASED);
            stockReservationRepository.save(reservation);
        }

        log.info("Released stock for order {} ({} reservations)", orderId, reservations.size());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ProductStock getStock(String sku) {
        return productStockRepository.findBySku(sku)
                .orElseThrow(NullPointerException::new /**new ProductNotFoundException(sku)*/);
    }

    /** Upserts a SKU's available quantity -- used by the demo/ops seeding endpoint. */
    @org.springframework.transaction.annotation.Transactional
    public ProductStock upsertStock(String sku, int availableQuantity) {
        ProductStock stock = productStockRepository.findBySku(sku)
                .orElseGet(() -> new ProductStock(sku, 0));
        stock.setAvailableQuantity(availableQuantity);
        return productStockRepository.save(stock);
    }
}
