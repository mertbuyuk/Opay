package com.opay.messaging;

import com.opay.events.order.OrderCreatedEvent;
import com.opay.events.topic.KafkaTopics;
import com.opay.service.InventoryService;
import com.opay.service.ReservationOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventListener {

    private final InventoryService inventoryService;
    private final InventoryEventPublisher inventoryEventPublisher;
    private final ServerHttpMessageConvertersCustomizer serverConvertersCustomizer;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED)
    public void onOrderCreated(OrderCreatedEvent orderCreatedEvent){
        log.info("Received OrderCreated for order {} ({} line items)", orderCreatedEvent.getOrderId(), orderCreatedEvent.getItems().size());

        ReservationOutcome reservationOutcome = inventoryService.reserveStock(orderCreatedEvent.getOrderId(),orderCreatedEvent.getItems());

        switch (reservationOutcome.type()){
            case ALREADY_PROCESSED -> {
                log.info("Order {} already processed -- not re-publishing", orderCreatedEvent.getOrderId());
            }
            case RESERVED -> {
                inventoryEventPublisher.publishInventoryReserved(orderCreatedEvent.getOrderId());
            }
            case OUT_OF_STOCK -> {
                inventoryEventPublisher.publishInventoryOutOfStock(orderCreatedEvent.getOrderId(),reservationOutcome.insufficientSku());
            }
        }
    }

}
