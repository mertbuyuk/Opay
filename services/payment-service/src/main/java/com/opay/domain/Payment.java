package com.opay.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String gatewayReference;
    private final String failureReason;
    private final int attemptCount;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Payment(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status,
                   String gatewayReference, String failureReason, int attemptCount,
                   Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.gatewayReference = gatewayReference;
        this.failureReason = failureReason;
        this.attemptCount = attemptCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Payment completed(UUID orderId, BigDecimal amount, String gatewayReference) {
        Instant now = Instant.now();
        return new Payment(UUID.randomUUID(), orderId, amount, PaymentStatus.COMPLETED,
                gatewayReference, null, 1, now, now);
    }

    public static Payment failed(UUID orderId, BigDecimal amount, String failureReason) {
        Instant now = Instant.now();
        return new Payment(UUID.randomUUID(), orderId, amount, PaymentStatus.FAILED,
                null, failureReason, 1, now, now);
    }

    /** Returns a new {@link Payment} representing a successful retry of a previously failed one. */
    public Payment withRetrySucceeded(String gatewayReference) {
        return new Payment(id, orderId, amount, PaymentStatus.COMPLETED,
                gatewayReference, null, attemptCount + 1, createdAt, Instant.now());
    }

    /** Returns a new {@link Payment} representing another failed retry attempt. */
    public Payment withRetryFailed(String failureReason) {
        return new Payment(id, orderId, amount, PaymentStatus.FAILED,
                null, failureReason, attemptCount + 1, createdAt, Instant.now());
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
