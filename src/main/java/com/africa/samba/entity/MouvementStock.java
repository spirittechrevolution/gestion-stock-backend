package com.africa.samba.entity;

import com.africa.samba.codeLists.TypeMouvement;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MouvementStock — traçabilité complète de tous les mouvements de stock.
 *
 * <p>Chaque entrée, sortie, correction ou transfert génère un enregistrement immuable. Permet de
 * reconstruire le stock à n'importe quel instant (audit trail).
 *
 * <p>stockAvant et stockApres sont des snapshots pour faciliter les audits.
 */
@Entity
@Table(name = "mouvements_stock", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStock extends BaseEntity {

  // ── Type et quantité ──────────────────────────────────────────────────

  @Enumerated(EnumType.STRING)
  @Column(name = "type_mouvement", nullable = false)
  private TypeMouvement typeMouvement;

  @NotNull
  @Min(1)
  @Column(nullable = false)
  private Integer quantite;

  /** Stock avant ce mouvement (snapshot) */
  @Column(name = "stock_avant", nullable = false)
  private Integer stockAvant;

  /** Stock après ce mouvement (snapshot) */
  @Column(name = "stock_apres", nullable = false)
  private Integer stockApres;

  // ── Contexte ──────────────────────────────────────────────────────────

  /**
   * Motif obligatoire pour les corrections manuelles. Ex : "Casse", "Vol", "Erreur de comptage",
   * "Inventaire physique"
   */
  @Size(max = 200)
  private String motif;

  /** Référence de la vente associée si SORTIE_VENTE */
  @Column(name = "reference_vente", length = 30)
  private String referenceVente;

  // ── Relations ─────────────────────────────────────────────────────────

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "produit_id", nullable = false)
  private Produit produit;

  /** Utilisateur ayant effectué le mouvement */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "utilisateur_id")
  private User utilisateur;

  // ── Méthodes métier ───────────────────────────────────────────────────

  /**
   * Quantité signée : positive si entrée, négative si sortie. Utile pour reconstituer l'historique.
   */
  public int getQuantiteSignee() {
    return typeMouvement.signe() * quantite;
  }
}
