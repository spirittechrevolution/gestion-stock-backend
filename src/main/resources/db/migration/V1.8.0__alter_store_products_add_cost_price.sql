-- ============================================================
-- V1.8.0 — Ajout du prix d'achat (cost_price) sur store_products
-- ============================================================
-- Permet de calculer la marge et le bénéfice par produit / catégorie.
-- Le champ est nullable : les anciens enregistrements restent valides.

ALTER TABLE administrative.store_products
    ADD COLUMN cost_price NUMERIC(12, 2);
