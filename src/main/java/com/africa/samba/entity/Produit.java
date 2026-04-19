package com.africa.samba.entity;

import com.africa.samba.codeLists.NiveauAlerte;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Produit — cœur du catalogue Samba POS.
 *
 * <p>Chaque produit appartient à une boutique. Il reçoit un QR code généré par le backend ZXing.
 * Son stock est décrémenté automatiquement à chaque vente validée.
 *
 * <p>Évolution Phase 2 : prix_grossiste, date_peremption, numero_serie déjà présents.
 */
@Entity
@Table(name = "produits", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit extends BaseEntity {

  // ── Informations de base ──────────────────────────────────────────────

  @NotBlank(message = "Le nom du produit est obligatoire")
  @Size(min = 1, max = 150)
  @Column(nullable = false, length = 150)
  private String nom;

  @Size(max = 500)
  private String description;

  // ── Prix ─────────────────────────────────────────────────────────────

  @NotNull(message = "Le prix de vente est obligatoire")
  @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
  @Column(name = "prix_vente", nullable = false, precision = 12, scale = 2)
  private BigDecimal prixVente;

  /** Prix d'achat — pour calcul de marge dans les rapports */
  @DecimalMin(value = "0.0")
  @Column(name = "prix_achat", precision = 12, scale = 2)
  private BigDecimal prixAchat;

  /** Prix grossiste — activé si client de type GROS (Phase 2 actif dès MVP) */
  @DecimalMin(value = "0.0")
  @Column(name = "prix_grossiste", precision = 12, scale = 2)
  private BigDecimal prixGrossiste;

  // ── Stock ─────────────────────────────────────────────────────────────

  @Min(0)
  @Column(name = "stock_actuel", nullable = false)
  @Builder.Default
  private Integer stockActuel = 0;

  /** Seuil en dessous duquel l'alerte FAIBLE est déclenchée */
  @Min(0)
  @Column(name = "stock_minimum")
  @Builder.Default
  private Integer stockMinimum = 0;

  // ── Identification ────────────────────────────────────────────────────

  /**
   * Code QR généré par ZXing (Spring Boot). Format : SAMBA-{boutiqueId}-{produitId}-{checksum}
   * Stocké comme chaîne — l'image PNG est sur S3.
   */
  @Column(name = "code_qr", unique = true, length = 100)
  private String codeQr;

  /** URL de l'image QR code PNG générée et stockée sur S3/Cloudinary */
  @Column(name = "qr_image_url")
  private String qrImageUrl;

  /**
   * Code-barres fabricant existant (EAN-13, EAN-8, CODE128…). Si renseigné, le codeQr pointe vers
   * ce code-barres natif.
   */
  @Column(name = "code_barres_fabricant", length = 50)
  private String codeBarresFabricant;

  /** Numéro de série — pour électronique (téléphones, tablettes…). Unique par boutique. */
  @Column(name = "numero_serie", length = 100)
  private String numeroSerie;

  // ── Photo ─────────────────────────────────────────────────────────────

  /** URL de la photo produit sur S3/Cloudinary */
  @Column(name = "photo_url")
  private String photoUrl;

  // ── Péremption (Phase 2 — présent dès MVP pour évolution) ────────────

  @Column(name = "date_peremption")
  private LocalDate datePeremption;

  // ── État ──────────────────────────────────────────────────────────────

  /** Produit actif = visible en caisse. Archivé = historique conservé. */
  @Column(nullable = false)
  @Builder.Default
  private Boolean actif = true;

  // ── Relations ─────────────────────────────────────────────────────────

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boutique_id", nullable = false)
  private Boutique boutique;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categorie_id")
  private Categorie categorie;

  @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
  @Builder.Default
  private List<LigneVente> lignesVente = new ArrayList<>();

  @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
  @Builder.Default
  private List<MouvementStock> mouvements = new ArrayList<>();

  // ── Méthodes métier ───────────────────────────────────────────────────

  /** Calcule le niveau d'alerte actuel du stock */
  public NiveauAlerte getNiveauAlerte() {
    return NiveauAlerte.calculer(stockActuel, stockMinimum != null ? stockMinimum : 0);
  }

  /** True si le produit expire dans les X prochains jours */
  public boolean expireDans(int jours) {
    if (datePeremption == null) return false;
    return !LocalDate.now().isAfter(datePeremption)
        && datePeremption.isBefore(LocalDate.now().plusDays(jours));
  }

  /** Calcule la marge brute unitaire */
  public BigDecimal getMargeUnitaire() {
    if (prixAchat == null) return BigDecimal.ZERO;
    return prixVente.subtract(prixAchat);
  }

  /** Décrémente le stock et vérifie la cohérence */
  public void decrementerStock(int quantite) {
    if (quantite <= 0) throw new IllegalArgumentException("Quantité doit être positive");
    if (this.stockActuel < quantite)
      throw new IllegalStateException("Stock insuffisant pour " + nom);
    this.stockActuel -= quantite;
  }

  /** Incrémente le stock (livraison, correction) */
  public void incrementerStock(int quantite) {
    if (quantite <= 0) throw new IllegalArgumentException("Quantité doit être positive");
    this.stockActuel += quantite;
  }

  public boolean hasCodeQr() {
    return codeQr != null && !codeQr.isBlank();
  }
}
