-- ============================================================================
-- V1.4.0__create_table_store_products.sql
-- Table store_products (catalogue par supérette) — sans FK, ajoutées par ALTER
-- ============================================================================

CREATE TABLE administrative.store_products (
    id              UUID            PRIMARY KEY,
    store_id        UUID            NOT NULL,
    product_id      UUID            NOT NULL,
    price           DECIMAL(12,2)   NOT NULL,
    stock           INTEGER         NOT NULL,
    stock_min       INTEGER,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,

    CONSTRAINT uk_store_product      UNIQUE (store_id, product_id),
    CONSTRAINT ck_sp_price_positive  CHECK (price > 0),
    CONSTRAINT ck_sp_stock_positive  CHECK (stock >= 0)
);

COMMENT ON TABLE administrative.store_products IS 'Catalogue par supérette — prix et stock définis par chaque magasin.';

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_store_products_store   ON administrative.store_products(store_id);
CREATE INDEX idx_store_products_product ON administrative.store_products(product_id);
CREATE INDEX idx_store_products_active  ON administrative.store_products(active);
