package com.opay.events.topic;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "orders.created";
    public static final String ORDER_CANCELLED = "orders.cancelled";
    public static final String PAYMENT_COMPLETED = "payments.completed";
    public static final String PAYMENT_FAILED = "payments.failed";
    /**
     * Dead letter topic: the raw, undeserializable-or-repeatedly-failing {@code orders.created}
     * record is republished here by Spring Kafka's {@code DeadLetterPublishingRecoverer} once
     * retries are exhausted, so it's never silently dropped and can be inspected/replayed later.
     * See {@code payment-service}'s Kafka consumer configuration.
     */
    public static final String PAYMENTS_DLQ = "payments.dlq";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String INVENTORY_OUT_OF_STOCK = "inventory.out-of-stock";
    private KafkaTopics() {
    }
}
