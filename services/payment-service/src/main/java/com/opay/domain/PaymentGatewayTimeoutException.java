package com.opay.domain;

public class PaymentGatewayTimeoutException extends RuntimeException {
    public PaymentGatewayTimeoutException(String message) {
        super(message);
    }
}
