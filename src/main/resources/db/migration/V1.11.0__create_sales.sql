-- V1.11.0__create_sales.sql
-- Table des ventes, liée à la session de caisse, au produit et au vendeur

CREATE TABLE administrative.sales (
    id UUID PRIMARY KEY,
    cash_register_session_id UUID NOT NULL REFERENCES administrative.cash_register_sessions(id) ON DELETE CASCADE,
    store_product_id UUID NOT NULL REFERENCES administrative.store_products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL,
    total_price NUMERIC(12,2) NOT NULL,
    sold_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_sales_session ON administrative.sales(cash_register_session_id);
CREATE INDEX idx_sales_store_product ON administrative.sales(store_product_id);
