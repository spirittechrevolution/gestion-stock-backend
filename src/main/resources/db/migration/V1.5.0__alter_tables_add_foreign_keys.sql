-- ============================================================================
-- V1.5.0__alter_tables_add_foreign_keys.sql
-- Ajout de toutes les clés étrangères (relations entre tables)
-- ============================================================================

-- ── barcodes → products ─────────────────────────────────────────────────────

ALTER TABLE administrative.barcodes
    ADD CONSTRAINT fk_barcode_product
    FOREIGN KEY (product_id) REFERENCES administrative.products(id);

-- ── user_roles → users ──────────────────────────────────────────────────────

ALTER TABLE administrative.user_roles
    ADD CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES administrative.users(id) ON DELETE CASCADE;

-- ── stores → users (owner) ──────────────────────────────────────────────────

ALTER TABLE administrative.stores
    ADD CONSTRAINT fk_store_owner
    FOREIGN KEY (owner_id) REFERENCES administrative.users(id);

-- ── store_products → stores ─────────────────────────────────────────────────

ALTER TABLE administrative.store_products
    ADD CONSTRAINT fk_sp_store
    FOREIGN KEY (store_id) REFERENCES administrative.stores(id);

-- ── store_products → products ───────────────────────────────────────────────

ALTER TABLE administrative.store_products
    ADD CONSTRAINT fk_sp_product
    FOREIGN KEY (product_id) REFERENCES administrative.products(id);
