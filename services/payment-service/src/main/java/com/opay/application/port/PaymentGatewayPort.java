package com.opay.application.port;

public interface PaymentGatewayPort {

    ChargeResult charge(ChargeRequest request);
}
