-- ============================================================================
-- V1.3.0__create_table_stores.sql
-- Table stores (supérettes) — sans FK, ajoutée par ALTER
-- ============================================================================

CREATE TABLE administrative.stores (
    id              UUID            PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    address         VARCHAR(255),
    phone           VARCHAR(20),
    active          BOOLEAN         NOT NULL,
    owner_id        UUID            NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL
);

COMMENT ON TABLE administrative.stores IS 'Supérettes — chaque supérette appartient à un propriétaire.';

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_stores_owner  ON administrative.stores(owner_id);
CREATE INDEX idx_stores_active ON administrative.stores(active);
