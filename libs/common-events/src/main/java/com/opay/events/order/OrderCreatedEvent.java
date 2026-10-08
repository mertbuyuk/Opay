package com.opay.events.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private UUID eventId;
    private UUID orderId;
    private UUID merchantId;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;
    private Instant occurredAt;
}
