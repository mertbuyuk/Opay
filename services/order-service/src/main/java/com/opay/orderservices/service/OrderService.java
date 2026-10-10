package com.opay.orderservices.service;

import com.opay.events.order.OrderItemEvent;
import com.opay.orderservices.Mapper.OrderMapper;
import com.opay.orderservices.dto.CreateOrderRequest;
import com.opay.orderservices.dto.OrderItemRequest;
import com.opay.orderservices.dto.OrderResponse;
import com.opay.orderservices.dto.PageResponse;
import com.opay.orderservices.exception.OrderNotFoundException;
import com.opay.orderservices.messaging.OrderCancelledApplicationEvent;
import com.opay.orderservices.messaging.OrderCreatedApplicationEvent;
import com.opay.orderservices.messaging.OrderDomainEventListener;
import com.opay.orderservices.model.Order;
import com.opay.orderservices.model.OrderItem;
import com.opay.orderservices.model.OrderStatus;
import com.opay.orderservices.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest orderRequest){
        Order order = Order.builder()
                .merchantId(orderRequest.getMerchantId())
                .orderStatus(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItemEvent> itemEvents = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getItems()){
            OrderItem orderItem = orderMapper.toOrderItem(itemRequest);
            order.addItem(orderItem);
            itemEvents.add(new OrderItemEvent(itemRequest.getSku(), itemRequest.getQuantity(), itemRequest.getUnitPrice()));
        }

        order.setTotalAmount(calculateTotalAmount(order.getOrderItems()));
        Order saved = orderRepository.save(order);
        OrderCreatedApplicationEvent event = new OrderCreatedApplicationEvent(
                order.getId(),
                order.getMerchantId(),
                order.getTotalAmount(),
                itemEvents
        );
        applicationEventPublisher.publishEvent(new OrderCreatedApplicationEvent(order.getId()
        ,order.getMerchantId(),order.getTotalAmount(),itemEvents));

        return orderMapper.toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId){
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return orderMapper.toOrderResponse(order);
    }
    /** transactional gerekli cünkü lazy cekiyoruz
     * tek session acilmali ve isteyince childlara ulasmaliyiz
     * transactional olmasa exception alabilirdik
     * spring.jpa.open-in-view
     *
     * paginnation
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listOrders(UUID merchantId, Pageable pageable){
        Page<Order> orders = merchantId != null
                ? orderRepository.findByMerchantId(merchantId,pageable)
                : orderRepository.findAll(pageable);

        Page<OrderResponse> orderResponses = orders.map(orderMapper::toOrderResponse);

        return PageResponse.from(orderResponses);
    }

    @Transactional
    public void markPaymentCompleted(UUID orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            log.warn("Received PaymentCompleted for unknown order {} - ignoring", orderId);
            return;
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED){
            log.warn("Order {} was already CANCELLED when PaymentCompleted arrived", orderId);
            return;
        }

        if (order.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
            log.info("Ignoring duplicate PaymentCompleted for order {} (status={})", orderId, order.getOrderStatus());
            return;
        }
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    @Transactional
    public void markPaymentFailed(UUID orderId, String reason){
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            log.warn("Received PaymentFailed for unknown order {} -- ignoring", orderId);
            return;
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            log.info("Ignoring duplicate PaymentFailed for already-cancelled order {}", orderId);
            return;
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderCancelledApplicationEvent(
                orderId, "payment failed" + reason
        ));
    }

    @Transactional
    public void markInventoryOutOfStock(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.warn("Received InventoryOutOfStock for unknown order {} -- ignoring", orderId);
            return;
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            log.info("Ignoring duplicate InventoryOutOfStock for already-cancelled order {}", orderId);
            return;
        }
        if (order.getOrderStatus() == OrderStatus.PAID) {
            log.warn("Order {} went out of stock after payment already completed -- this order "
                    + "needs a manual refund (refund flow is out of scope for Phase 3, see "
                    + "docs/decisions/adr/0004)", orderId);
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public BigDecimal calculateTotalAmount(List<OrderItem> orderItemList){
        return orderItemList.stream().map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
