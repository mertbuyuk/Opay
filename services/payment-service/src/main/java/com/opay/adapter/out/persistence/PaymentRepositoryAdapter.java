package com.opay.adapter.out.persistence;

import com.opay.application.port.PaymentRepositoryPort;
import com.opay.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return jpaRepository.findById(orderId).map(this::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        try {
            PaymentJpaEntity saved = jpaRepository.save(toEntity(payment));
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            // The UNIQUE constraint on order_id
            return jpaRepository.findByOrderId(payment.getOrderId())
                    .map(this::toDomain)
                    .orElseThrow(() -> ex);
        }
    }

    private PaymentJpaEntity toEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getGatewayReference(),
                payment.getFailureReason(),
                payment.getAttemptCount(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    private Payment toDomain(PaymentJpaEntity entity) {
               return new Payment(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getGatewayReference(),
                entity.getFailureReason(),
                entity.getAttemptCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
