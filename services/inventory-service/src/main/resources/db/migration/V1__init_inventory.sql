CREATE TABLE product_stock (
    sku               VARCHAR(100) PRIMARY KEY,
    available_quantity INTEGER NOT NULL CHECK (available_quantity >= 0),
    reserved_quantity  INTEGER NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- One row per (order, sku) reservation, not one row per order -- an order can reserve several
-- SKUs, and the compensating release (OrderCancelled -> release) needs to know exactly which
-- SKUs and quantities to give back, not just "this order had a reservation."
CREATE TABLE stock_reservations (
    id          UUID PRIMARY KEY,
    order_id    UUID NOT NULL,
    sku         VARCHAR(100) NOT NULL,
    quantity    INTEGER NOT NULL,
    status      VARCHAR(20) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- Idempotency guard: if the same OrderCreatedEvent is redelivered, a second reservation
    -- attempt for the same (order_id, sku) must be recognizable as "already handled," not
    -- decrement stock a second time.
    CONSTRAINT uq_stock_reservations_order_sku UNIQUE (order_id, sku)
);

CREATE INDEX idx_stock_reservations_order_id ON stock_reservations (order_id);
