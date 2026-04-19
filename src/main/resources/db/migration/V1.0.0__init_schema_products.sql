-- ============================================================================
-- V1.0.0__init_schema_products.sql
-- Initialisation du schéma + table products (catalogue global)
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS administrative;

-- ── Products (catalogue global partagé) ─────────────────────────────────────

CREATE TABLE administrative.products (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(150)    NOT NULL,
    brand           VARCHAR(100),
    category        VARCHAR(100)    NOT NULL,
    description     TEXT,
    image_url       VARCHAR(500),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

COMMENT ON TABLE administrative.products IS 'Catalogue global de produits — partagé entre toutes les supérettes. AUCUN PRIX ICI.';

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_products_category ON administrative.products(category);
CREATE INDEX idx_products_name     ON administrative.products(name);
CREATE INDEX idx_products_active   ON administrative.products(active);
