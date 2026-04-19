-- V1.10.0__create_cash_register_sessions.sql
-- Ajoute la table des sessions de caisse

CREATE TABLE administrative.cash_register_sessions (
    id UUID PRIMARY KEY,
    cash_register_id UUID NOT NULL REFERENCES administrative.cash_registers(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES administrative.users(id),
    opened_by_id UUID NOT NULL REFERENCES administrative.users(id),
    opened_at TIMESTAMP NOT,
    closed_at TIMESTAMP,
    created_at TIMESTAMP NOT,
    updated_at TIMESTAMP NOT,
    CONSTRAINT uk_cash_register_active_session UNIQUE (cash_register_id) WHERE closed_at IS NULL
);

-- Un vendeur ne peut avoir qu'une session active par supérette
CREATE UNIQUE INDEX uk_user_active_session_per_store ON administrative.cash_register_sessions (
    user_id, 
    (SELECT store_id FROM administrative.cash_registers cr WHERE cr.id = cash_register_sessions.cash_register_id)
) WHERE closed_at IS NULL;
