-- ============================================================================
-- V1.6.0__create_table_store_members.sql
-- Table d'association membres ↔ supérettes (employés et managers)
-- ============================================================================

CREATE TABLE administrative.store_members (
    id              UUID            PRIMARY KEY,
    store_id        UUID            NOT NULL,
    user_id         UUID            NOT NULL,
    role            VARCHAR(20)     NOT NULL,
    active          BOOLEAN         NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,

    CONSTRAINT uk_store_member UNIQUE (store_id, user_id)
);

COMMENT ON TABLE administrative.store_members IS 'Association employés/managers ↔ supérettes. Le OWNER est lié via stores.owner_id.';

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_store_members_store_id ON administrative.store_members(store_id);
CREATE INDEX idx_store_members_user_id  ON administrative.store_members(user_id);
CREATE INDEX idx_store_members_active   ON administrative.store_members(active);

-- ── Foreign keys ────────────────────────────────────────────────────────────

ALTER TABLE administrative.store_members
    ADD CONSTRAINT fk_store_member_store
    FOREIGN KEY (store_id) REFERENCES administrative.stores(id);

ALTER TABLE administrative.store_members
    ADD CONSTRAINT fk_store_member_user
    FOREIGN KEY (user_id) REFERENCES administrative.users(id);
