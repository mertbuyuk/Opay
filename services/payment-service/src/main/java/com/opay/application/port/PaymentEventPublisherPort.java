package com.opay.application.port;

import com.opay.domain.Payment;

public interface PaymentEventPublisherPort {

    void publishPaymentCompleted(Payment payment);

    void publishPaymentFailed(Payment payment);
}
