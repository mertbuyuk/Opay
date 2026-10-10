-- The UNIQUE constraint on order_id is the actual idempotency guarantee, not just a hint: even
-- if two threads/instances race to process the same OrderCreatedEvent (e.g. during a consumer
-- rebalance), only one INSERT can win. See PaymentRepositoryAdapter for how the resulting
-- DataIntegrityViolationException is handled as "someone else already processed this," not as
-- an error.
CREATE TABLE payments (
                          id               UUID PRIMARY KEY,
                          order_id         UUID NOT NULL,
                          amount           NUMERIC(19, 2) NOT NULL,
                          status           VARCHAR(30) NOT NULL,
                          gateway_reference VARCHAR(100),
                          failure_reason   VARCHAR(500),
                          attempt_count    INTEGER NOT NULL DEFAULT 1,
                          created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
                          CONSTRAINT uq_payments_order_id UNIQUE (order_id)
);

-- Every read this service does is "find the payment for this order" -- the UNIQUE constraint
-- above already creates a supporting index, so no separate index is needed here. This comment
-- exists so the next person reading this migration doesn't wonder why there isn't one.
