package com.opay.adapter.out.kafka;

import com.opay.application.port.PaymentEventPublisherPort;
import com.opay.domain.Payment;
import com.opay.events.payment.PaymentCompletedEvent;
import com.opay.events.payment.PaymentFailedEvent;
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
public class PaymentEventPublisherAdapter implements PaymentEventPublisherPort {

    private final KafkaTemplate<String,Object> kafkaTemplate;


    @Override
    public void publishPaymentCompleted(Payment payment) {
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                UUID.randomUUID(),payment.getOrderId(),payment.getId(),payment.getAmount(), Instant.now()
        );
        log.info("Publishing PaymentCompleted for order {}", payment.getOrderId());
        kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED,event.getOrderId().toString(),event);
    }

    @Override
    public void publishPaymentFailed(Payment payment) {
        PaymentFailedEvent event = new PaymentFailedEvent(
                UUID.randomUUID(), payment.getOrderId(), payment.getFailureReason(), Instant.now());
        log.info("Publishing PaymentFailed for order {} (reason={})", payment.getOrderId(), payment.getFailureReason());
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, payment.getOrderId().toString(), event);

    }
}
