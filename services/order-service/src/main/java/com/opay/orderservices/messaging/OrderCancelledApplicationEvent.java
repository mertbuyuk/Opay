package com.opay.orderservices.messaging;

import java.util.UUID;

public record OrderCancelledApplicationEvent(
        UUID orderId, String reason) {
}

