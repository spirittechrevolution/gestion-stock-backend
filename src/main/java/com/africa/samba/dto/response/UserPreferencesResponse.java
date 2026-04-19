package com.africa.samba.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserPreferencesResponse(
    UUID id,
    UUID userId,

    // ── Interface / Affichage ─────────────────────────────────────────────
    String langue,
    String theme,
    String taillePolice,

    // ── Caisse ────────────────────────────────────────────────────────────
    Boolean sonScanActif,
    Boolean vibrationScanActif,
    String modePaiementDefaut,
    Boolean afficherRecuAuto,
    String partageRecuDefaut,
    Boolean confirmerViderPanier,
    Boolean autoIncrementScan,

    // ── Catalogue ─────────────────────────────────────────────────────────
    String vueCatalogue,
    String triProduitsDefaut,
    Boolean afficherProduitsRupture,

    // ── Notifications ─────────────────────────────────────────────────────
    Boolean notifStockFaible,
    Boolean notifRuptureStock,
    Boolean notifPeremption,
    Boolean notifBilanJournalier,
    String heureBilanJournalier,

    // ── Session et sécurité ───────────────────────────────────────────────
    Integer timeoutSessionMinutes,
    Boolean pinAchaqueOuverture,

    // ── Dashboard / Rapports ──────────────────────────────────────────────
    String periodeRapportDefaut,
    String devise,

    // ── Audit ────────────────────────────────────────────────────────────
    LocalDateTime updatedAt) {}
