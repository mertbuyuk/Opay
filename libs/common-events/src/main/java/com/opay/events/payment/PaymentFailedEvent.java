package com.opay.events.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by {@code payment-service} whenever a payment ends in a state that will never
 * become {@code PAID} without external intervention -- either a business decline (e.g. amount
 * over the mock gateway's limit, not retried at all) or a transient failure that exhausted its
 * retry budget (see {@code payment-service}'s Kafka consumer error-handler config). Consumed by
 * {@code order-service}, which cancels the order and publishes the compensating
 * {@code OrderCancelledEvent} for Inventory Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    private UUID eventId;
    private UUID orderId;
    private String reason;
    private Instant occurredAt;
}

