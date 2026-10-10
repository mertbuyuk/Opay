package com.opay.adapter.out.gateway;

import com.opay.application.port.ChargeRequest;
import com.opay.application.port.ChargeResult;
import com.opay.application.port.PaymentGatewayPort;
import com.opay.domain.PaymentDeclinedException;
import com.opay.domain.PaymentGatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class MockPaymentGatewayAdapter implements PaymentGatewayPort {

    private final BigDecimal declineAboveAmount;
    private final int flakyAttempts;
    private final Map<UUID, AtomicInteger> attemptsByOrder = new ConcurrentHashMap<>();

    public MockPaymentGatewayAdapter(
            @Value("${orbitpay.payment.gateway.decline-above-amount}") BigDecimal declineAboveAmount,
            @Value("${orbitpay.payment.gateway.flaky-attempts}") int flakyAttempts) {
        this.declineAboveAmount = declineAboveAmount;
        this.flakyAttempts = flakyAttempts;
    }

    @Override
    public ChargeResult charge(ChargeRequest request) {
        if (request.amount().compareTo(declineAboveAmount) > 0) {
            log.warn("Mock gateway declining order {}: amount {} exceeds decline-above-amount {}",
                    request.orderId(), request.amount(), declineAboveAmount);
            throw new PaymentDeclinedException(
                    "amount " + request.amount() + " exceeds the mock gateway's limit of " + declineAboveAmount);
        }

        int attempt = attemptsByOrder.computeIfAbsent(request.orderId(), id -> new AtomicInteger(0))
                .incrementAndGet();
        if (attempt <= flakyAttempts) {
            log.warn("Mock gateway simulating a transient failure for order {} (attempt {}/{})",
                    request.orderId(), attempt, flakyAttempts);
            throw new PaymentGatewayTimeoutException(
                    "simulated gateway timeout on attempt " + attempt + " of " + flakyAttempts);
        }

        String gatewayReference = "mock-gw-" + UUID.randomUUID();
        log.info("Mock gateway approved order {} on attempt {} (reference={})", request.orderId(), attempt, gatewayReference);
        return new ChargeResult(gatewayReference);
    }
}
