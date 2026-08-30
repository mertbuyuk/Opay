CREATE TABLE orders(
    id              UUID PRIMARY KEY,
    merchant_id     UUID NOT NULL,
    order_status    VARCHAR(30) NOT NULL,
    total_amount    NUMERIC(19,2) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

--en cok sorulan sorgular icin index  "my orders in status X"
CREATE INDEX idx_orders_merchant_id_status ON orders(merchant_id,order_status);

CREATE TABLE order_items(
    id          UUID PRIMARY KEY,
    order_id    UUID NOT NULL  REFERENCES orders (id) ON DELETE CASCADE,
    sku         VARCHAR(100) NOT NULL,
    quantity    INTEGER NOT NULL,
    unit_price  NUMERIC(19,2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);