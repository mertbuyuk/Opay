package com.opay.application;

import com.opay.application.port.*;
import com.opay.domain.Payment;
import com.opay.domain.PaymentNotFoundException;
import com.opay.domain.PaymentStatus;
import com.opay.events.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;

    @Transactional
    public void process(OrderCreatedEvent event){
        Optional<Payment> existing = paymentRepositoryPort.findByOrderId(event.getOrderId());

        if(existing.isPresent()){
            log.info("Ignoring duplicate OrderCreated for order {} -- payment already recorded (status={})",
                    event.getOrderId(), existing.get().getStatus());
            return;
        }

        ChargeResult result = paymentGatewayPort.charge(new ChargeRequest(event.getOrderId(),event.getTotalAmount()));

        Payment payment = Payment.completed(event.getOrderId(),event.getTotalAmount(),result.gatewayReference());
        paymentRepositoryPort.save(payment);
        paymentEventPublisherPort.publishPaymentCompleted(payment);
    }

    @Transactional
    public void recordFailureAfterExhaustion(UUID orderId, BigDecimal amount, String reason) {
        Optional<Payment> existing = paymentRepositoryPort.findByOrderId(orderId);
        if (existing.isPresent()) {
            log.info("Order {} already has a terminal payment record (status={}) -- not overwriting after exhaustion",
                    orderId, existing.get().getStatus());
            return;
        }
        Payment payment = Payment.failed(orderId, amount, reason);
        paymentRepositoryPort.save(payment);
        paymentEventPublisherPort.publishPaymentFailed(payment);
    }

    @Transactional(readOnly = true)
    public Payment getByOrderId(UUID orderId) {
        return paymentRepositoryPort.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
    }

    @Transactional
    public Payment retry(UUID orderId) {
        Payment existing = paymentRepositoryPort.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));

        if (existing.getStatus() == PaymentStatus.COMPLETED) {
            log.info("Retry requested for order {} but it's already COMPLETED -- no-op", orderId);
            return existing;
        }

        Payment result;
        try {
            ChargeResult chargeResult = paymentGatewayPort.charge(new ChargeRequest(orderId, existing.getAmount()));
            result = existing.withRetrySucceeded(chargeResult.gatewayReference());
            paymentEventPublisherPort.publishPaymentCompleted(result);
        } catch (RuntimeException ex) {
            result = existing.withRetryFailed(ex.getMessage());
            paymentEventPublisherPort.publishPaymentFailed(result);
        }
        return paymentRepositoryPort.save(result);
    }
}
