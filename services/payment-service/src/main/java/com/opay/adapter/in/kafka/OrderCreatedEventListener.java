package com.opay.adapter.in.kafka;

import com.opay.application.PaymentProcessingService;
import com.opay.events.order.OrderCreatedEvent;
import com.opay.events.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventListener {

    private final PaymentProcessingService paymentProcessingService;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED)
    public void onOrderCreated(OrderCreatedEvent event){
        log.info("Received OrderCreated for order {} (amount={})", event.getOrderId(), event.getTotalAmount());
        paymentProcessingService.process(event);
    }

}
