package com.opay.adapter.in.web;

import com.opay.domain.Payment;
import com.opay.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String gatewayReference;
    private String failureReason;
    private int attemptCount;
    private Instant updatedAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .gatewayReference(payment.getGatewayReference())
                .failureReason(payment.getFailureReason())
                .attemptCount(payment.getAttemptCount())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
