package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LigneVente — détail d'un produit dans une vente.
 *
 * <p>Le prix unitaire est dupliqué au moment de la vente pour conserver l'historique même si le
 * prix du produit change ensuite.
 *
 * <p>La remise ligne (optionnelle) est en FCFA — distincte de la remise globale sur la Vente.
 */
@Entity
@Table(name = "lignes_vente", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneVente extends BaseEntity {

  // ── Quantité ──────────────────────────────────────────────────────────

  @NotNull
  @Min(value = 1, message = "La quantité doit être au moins 1")
  @Column(nullable = false)
  private Integer quantite;

  // ── Prix snapshots au moment de la vente ──────────────────────────────

  /**
   * Prix unitaire appliqué au moment de la vente. Snapshot immuable — ne change pas si le prix
   * produit évolue.
   */
  @NotNull
  @DecimalMin("0.0")
  @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
  private BigDecimal prixUnitaire;

  /**
   * Remise ligne en FCFA (ex: -500 FCFA sur cette ligne). Distincte de la remise globale sur la
   * Vente.
   */
  @DecimalMin("0.0")
  @Column(name = "remise_ligne", precision = 10, scale = 2)
  @Builder.Default
  private BigDecimal remiseLigne = BigDecimal.ZERO;

  /**
   * Nom du produit au moment de la vente (snapshot). Utile si le produit est supprimé ou renommé
   * ultérieurement.
   */
  @Column(name = "nom_produit_snapshot", length = 150)
  private String nomProduitSnapshot;

  // ── Relations ─────────────────────────────────────────────────────────

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vente_id", nullable = false)
  private Vente vente;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "produit_id", nullable = false)
  private Produit produit;

  // ── Méthodes métier ───────────────────────────────────────────────────

  /** Sous-total de la ligne après remise */
  public BigDecimal getSousTotal() {
    BigDecimal brut = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    BigDecimal rem = remiseLigne != null ? remiseLigne : BigDecimal.ZERO;
    return brut.subtract(rem);
  }

  /** Capture le nom du produit au moment de la vente */
  public void snapshotNomProduit() {
    if (produit != null) {
      this.nomProduitSnapshot = produit.getNom();
    }
  }
}
