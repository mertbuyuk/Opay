package com.opay.orderservices.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderDomainEventListener {
    private final OrderEventPublisher orderEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedApplicationEvent orderCreatedApplicationEvent){
        orderEventPublisher.publishOrderCreated(orderCreatedApplicationEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCancelled(OrderCancelledApplicationEvent orderCancelledApplicationEvent){
        orderEventPublisher.publishOrderCancelled(orderCancelledApplicationEvent);
    }
}
