package com.opay.application.port;

import com.opay.domain.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {

    Optional<Payment> findByOrderId(UUID orderId);

    Payment save(Payment payment);

}
