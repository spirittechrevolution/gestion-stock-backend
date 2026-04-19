-- V1.9.0__create_cash_registers.sql
-- Ajoute la table des caisses par supérette

CREATE TABLE administrative.cash_registers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL REFERENCES administrative.stores(id) ON DELETE CASCADE,
    number INTEGER NOT NULL,
    label VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_store_cash_number UNIQUE (store_id, number)
);
