-- Phase 2: the most common paginated query is "this merchant's orders,
-- newest first" (GET /orders?merchantId=...&sort=createdAt,desc). The Phase 1
-- (merchant_id, status) index doesn't help sort ordering -- this one does.
CREATE INDEX idx_orders_merchant_id_created_at ON orders (merchant_id, created_at DESC);
