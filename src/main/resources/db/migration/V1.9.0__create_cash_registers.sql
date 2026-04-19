-- V1.9.0__create_cash_registers.sql
-- Ajoute la table des caisses par supérette

CREATE TABLE administrative.cash_registers (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL REFERENCES administrative.stores(id) ON DELETE CASCADE,
    number INTEGER NOT NULL,
    label VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_store_cash_number UNIQUE (store_id, number)
);
