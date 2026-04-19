-- ============================================================================
-- V1.2.0__create_table_users.sql
-- Table users + table de collection user_roles
-- ============================================================================

CREATE TABLE administrative.users (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id         VARCHAR(100)    NOT NULL UNIQUE,
    first_name          VARCHAR(100)    NOT NULL,
    last_name           VARCHAR(100)    NOT NULL,
    email               VARCHAR(100),
    phone               VARCHAR(20),
    date_of_birth       DATE,
    address             TEXT,
    city                VARCHAR(100),
    country             VARCHAR(5)      DEFAULT 'SN',
    language            VARCHAR(5)      DEFAULT 'fr',
    avatar_url          VARCHAR(500),
    is_email_verified   BOOLEAN         NOT NULL DEFAULT FALSE,
    is_phone_verified   BOOLEAN         NOT NULL DEFAULT FALSE,
    pin_hash            VARCHAR(72)     NOT NULL,
    token_session       VARCHAR(512),
    derniere_connexion  TIMESTAMP,
    deleted_at          TIMESTAMP,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT now()
);

-- ── User roles (table de collection) ────────────────────────────────────────
-- Valeurs : OWNER, MANAGER, EMPLOYEE, ADMIN

CREATE TABLE administrative.user_roles (
    user_id     UUID            NOT NULL,
    role        VARCHAR(30)     NOT NULL,

    PRIMARY KEY (user_id, role)
);

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_users_email        ON administrative.users(email);
CREATE INDEX idx_users_phone        ON administrative.users(phone);
CREATE INDEX idx_users_keycloak_id  ON administrative.users(keycloak_id);
CREATE INDEX idx_users_active       ON administrative.users(is_active);
CREATE INDEX idx_user_roles_role    ON administrative.user_roles(role);
