-- Demo/local-dev seed data only -- a real deployment would never seed product stock via a
-- schema migration. Exists so the Phase 3 end-to-end flow (see the root README's "Try the full
-- flow" section) has something to reserve against without a manual setup step first; more SKUs
-- can be added at runtime via POST /inventory/stock (see InventoryController).
INSERT INTO product_stock (sku, available_quantity) VALUES
    ('SKU-1', 100),
    ('SKU-2', 50),
    ('SKU-9', 5),
    ('SKU-123', 25);
