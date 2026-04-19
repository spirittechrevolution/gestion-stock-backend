-- V1.12.0__create_audit_log.sql
-- Table d'audit pour tracer toutes les actions importantes

CREATE TABLE administrative.audit_log (
    id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL, -- ex: SESSION_OPEN, SESSION_CLOSE, SALE_CREATE, CASH_REGISTER_UPDATE
    user_id UUID REFERENCES administrative.users(id),
    store_id UUID REFERENCES administrative.stores(id),
    cash_register_id UUID REFERENCES administrative.cash_registers(id),
    session_id UUID REFERENCES administrative.cash_register_sessions(id),
    sale_id UUID REFERENCES administrative.sales(id),
    details TEXT, -- JSON ou texte libre pour stocker les infos complémentaires
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_log_store ON administrative.audit_log(store_id);
CREATE INDEX idx_audit_log_user ON administrative.audit_log(user_id);
CREATE INDEX idx_audit_log_event_type ON administrative.audit_log(event_type);
