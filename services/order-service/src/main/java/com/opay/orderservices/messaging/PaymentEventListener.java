package com.opay.orderservices.messaging;

import com.opay.events.payment.PaymentCompletedEvent;
import com.opay.events.payment.PaymentFailedEvent;
import com.opay.events.topic.KafkaTopics;
import com.opay.orderservices.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event){
        log.info("Received PaymentCompleted for order {}", event.getOrderId());
        orderService.markPaymentCompleted(event.getOrderId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event){
        log.info("Received PaymentCompleted for order {}", event.getOrderId());
        orderService.markPaymentFailed(event.getOrderId(),event.getReason());
    }
}
