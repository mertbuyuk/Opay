package com.opay.domain;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(UUID orderId) {
        super("No payment record found for order: " + orderId);
    }
}
