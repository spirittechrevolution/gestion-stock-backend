package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SyncQueue — file d'attente de synchronisation offline → backend.
 *
 * <p>Quand l'app mobile fonctionne sans réseau, chaque opération (vente, correction stock…) est
 * mise en file d'attente. Dès que le réseau revient, le syncService envoie les opérations par ordre
 * chronologique et marque chacune comme traitée.
 *
 * <p>La règle de conflit : le backend a toujours raison pour les données de référence (produits,
 * prix). Les ventes ne créent jamais de conflit car elles sont append-only.
 */
@Entity
@Table(name = "sync_queue", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncQueue extends BaseEntity {

  // ── Opération en attente ──────────────────────────────────────────────

  /** Type d'opération : VENTE_CREATE, STOCK_CORRECTION, PRODUIT_CREATE, PRODUIT_UPDATE… */
  @Column(nullable = false, length = 50)
  private String typeOperation;

  /**
   * Payload JSON de l'opération (sérialisé côté mobile). Ex: {"venteId":"local-abc123",
   * "total":15000, ...}
   */
  @Column(nullable = false, columnDefinition = "TEXT")
  private String payloadJson;

  /** ID local mobile de l'entité concernée */
  @Column(name = "id_local", length = 64)
  private String idLocal;

  // ── Statut de traitement ──────────────────────────────────────────────

  @Column(nullable = false)
  @Builder.Default
  private Boolean traitee = false;

  @Column(name = "traitee_at")
  private LocalDateTime traiteeAt;

  /** Nombre de tentatives d'envoi (pour retry avec backoff) */
  @Column(name = "nb_tentatives")
  @Builder.Default
  private Integer nbTentatives = 0;

  /** Message d'erreur si la dernière tentative a échoué */
  @Column(name = "erreur", columnDefinition = "TEXT")
  private String erreur;

  // ── Relation boutique ─────────────────────────────────────────────────

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boutique_id", nullable = false)
  private Boutique boutique;

  // ── Méthodes métier ───────────────────────────────────────────────────

  public void marquerTraitee() {
    this.traitee = true;
    this.traiteeAt = LocalDateTime.now();
    this.erreur = null;
  }

  public void enregistrerEchec(String message) {
    this.nbTentatives++;
    this.erreur = message;
  }

  public boolean peutRetenter(int maxTentatives) {
    return !traitee && nbTentatives < maxTentatives;
  }
}
