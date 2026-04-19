-- ============================================================================
-- V1.7.0__alter_products_add_status.sql
-- Ajout du statut (PENDING/APPROVED) et de la supérette d'origine sur products
-- ============================================================================

-- ── Colonne status ──────────────────────────────────────────────────────────

ALTER TABLE administrative.products
    ADD COLUMN status VARCHAR(20) NOT NULL;

COMMENT ON COLUMN administrative.products.status IS 'PENDING = créé à la volée par un employé, APPROVED = validé et visible dans le catalogue global';

-- ── Colonne created_by_store_id ─────────────────────────────────────────────

ALTER TABLE administrative.products
    ADD COLUMN created_by_store_id UUID;

ALTER TABLE administrative.products
    ADD CONSTRAINT fk_product_created_by_store
    FOREIGN KEY (created_by_store_id) REFERENCES administrative.stores(id);

COMMENT ON COLUMN administrative.products.created_by_store_id IS 'Supérette d''origine quand le produit a été créé à la volée par un employé. NULL pour les produits créés par un admin.';

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_products_status ON administrative.products(status);
