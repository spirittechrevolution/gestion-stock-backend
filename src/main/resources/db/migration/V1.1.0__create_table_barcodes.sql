-- ============================================================================
-- V1.1.0__create_table_barcodes.sql
-- Table barcodes (codes-barres uniques) — sans FK, ajoutée par ALTER
-- ============================================================================

CREATE TABLE administrative.barcodes (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50)     NOT NULL,
    type            VARCHAR(20)     NOT NULL DEFAULT 'EAN',
    product_id      UUID            NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now(),

    CONSTRAINT uk_barcode_code  UNIQUE (code),
    CONSTRAINT ck_barcode_type  CHECK (type IN ('EAN', 'INTERNAL'))
);

COMMENT ON TABLE administrative.barcodes IS 'Codes-barres — 1 code = 1 seul produit, 1 produit peut avoir N codes.';
COMMENT ON COLUMN administrative.barcodes.type IS 'EAN = officiel GS1 | INTERNAL = généré par Samba (préfixe 2)';

-- ── Index ───────────────────────────────────────────────────────────────────

CREATE INDEX idx_barcodes_product ON administrative.barcodes(product_id);
CREATE INDEX idx_barcodes_code    ON administrative.barcodes(code);
