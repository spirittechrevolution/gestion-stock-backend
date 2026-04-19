package com.africa.samba.entity;

import com.africa.samba.codeLists.NiveauAlerte;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * StoreProduct — catalogue d'une supérette.
 *
 * <p>Table de liaison entre {@link Store} et {@link Product} qui porte le <strong>prix</strong> et
 * le <strong>stock</strong> propres à chaque supérette.
 *
 * <p>Contrainte : {@code UNIQUE(store_id, product_id)} — un produit ne peut apparaître qu'une seule
 * fois dans le catalogue d'une supérette.
 */
@Entity
@Table(
    name = "store_products",
    schema = "administrative",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_store_product",
            columnNames = {"store_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StoreProduct extends BaseEntity {

  // ── Relations ─────────────────────────────────────────────────────────

  /** Supérette qui référence ce produit */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  /** Produit du catalogue global */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  // ── Prix et stock (propres à la supérette) ────────────────────────────

  /** Prix de vente dans cette supérette (en FCFA) */
  @NotNull(message = "Le prix est obligatoire")
  @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  /** Prix d'achat fournisseur dans cette supérette (en FCFA) */
  @DecimalMin(value = "0.0", inclusive = false, message = "Le prix d'achat doit être positif")
  @Column(name = "cost_price", precision = 12, scale = 2)
  private BigDecimal costPrice;

  /** Stock disponible dans cette supérette */
  @Min(0)
  @Column(nullable = false)
  @Builder.Default
  private Integer stock = 0;

  /** Seuil d'alerte de stock faible */
  @Min(0)
  @Column(name = "stock_min")
  @Builder.Default
  private Integer stockMin = 0;

  /** Produit actif dans cette supérette */
  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  // ── Méthodes métier ───────────────────────────────────────────────────

  /** Calcule la marge unitaire (prix de vente − prix d'achat). Null si pas de prix d'achat. */
  public BigDecimal getMargin() {
    if (costPrice == null) return null;
    return price.subtract(costPrice);
  }

  /** Calcule le niveau d'alerte actuel du stock */
  public NiveauAlerte getNiveauAlerte() {
    return NiveauAlerte.calculer(stock, stockMin != null ? stockMin : 0);
  }

  /** Décrémente le stock après une vente */
  public void decrementerStock(int quantite) {
    if (quantite <= 0) throw new IllegalArgumentException("Quantité doit être positive");
    if (this.stock < quantite) throw new IllegalStateException("Stock insuffisant");
    this.stock -= quantite;
  }

  /** Incrémente le stock (livraison, correction) */
  public void incrementerStock(int quantite) {
    if (quantite <= 0) throw new IllegalArgumentException("Quantité doit être positive");
    this.stock += quantite;
  }
}
