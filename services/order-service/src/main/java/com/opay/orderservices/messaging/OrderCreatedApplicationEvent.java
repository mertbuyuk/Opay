package com.opay.orderservices.messaging;

import com.opay.events.order.OrderItemEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedApplicationEvent(
        UUID orderId,
        UUID merchantId,
        BigDecimal totalAmount,
        List<OrderItemEvent> items
) {
}