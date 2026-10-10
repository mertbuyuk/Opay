package com.opay.events.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private UUID eventId;
    private UUID orderId;
    private UUID paymentId;
    private BigDecimal amount;
    private Instant occurredAt;
}
