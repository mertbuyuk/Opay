package com.opay.orderservices.messaging;

import com.opay.events.order.OrderCancelledEvent;
import com.opay.events.order.OrderCreatedEvent;
import com.opay.events.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedApplicationEvent orderCreatedApplicationEvent){
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                UUID.randomUUID(),
                orderCreatedApplicationEvent.orderId(),
                orderCreatedApplicationEvent.merchantId(),
                orderCreatedApplicationEvent.totalAmount(),
                orderCreatedApplicationEvent.items(),
                Instant.now()
        );
        log.info("Publishing OrderCreated for order {}", orderCreatedApplicationEvent.orderId());
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED,orderCreatedEvent.getOrderId().toString(),orderCreatedEvent);
    }

    public void publishOrderCancelled(OrderCancelledApplicationEvent orderCancelledApplicationEvent){
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(
                UUID.randomUUID(),
                orderCancelledApplicationEvent.orderId(),
                orderCancelledApplicationEvent.reason(),
                Instant.now()
        );
        log.info("Publishing OrderCancelled for order {} (reason={})", orderCancelledEvent.getOrderId(), orderCancelledEvent.getReason());
        kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED,orderCancelledEvent.getOrderId().toString(),orderCancelledEvent);
    }
}
