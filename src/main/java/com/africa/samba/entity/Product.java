package com.africa.samba.entity;

import com.africa.samba.codeLists.ProductStatus;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Produit — catalogue global partagé entre toutes les supérettes.
 *
 * <p>Contient uniquement les informations descriptives du produit (nom, marque, catégorie).
 * <strong>Aucun prix ni stock ici</strong> — ces données sont définies par chaque supérette dans
 * {@link StoreProduct}.
 *
 * <p>Un produit peut avoir plusieurs codes-barres ({@link Barcode}) et être référencé par plusieurs
 * supérettes ({@link StoreProduct}).
 */
@Entity
@Table(name = "products", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

  // ── Informations descriptives ─────────────────────────────────────────

  /** Nom du produit (ex : "Lait Nido 400g", "Riz Oncle Sam 5kg") */
  @NotBlank(message = "Le nom du produit est obligatoire")
  @Size(min = 1, max = 150)
  @Column(nullable = false, length = 150)
  private String name;

  /** Marque du produit (ex : "Nestlé", "Banga") — nullable pour les produits locaux/vrac */
  @Size(max = 100)
  @Column(length = 100)
  private String brand;

  /** Catégorie du produit (ex : "Alimentaire", "Hygiène", "Boissons") */
  @NotBlank(message = "La catégorie est obligatoire")
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String category;

  /** Description optionnelle du produit */
  @Size(max = 500)
  @Column(columnDefinition = "TEXT")
  private String description;

  /** URL de la photo du produit (stockée sur MinIO/S3) */
  @Column(name = "image_url", length = 500)
  private String imageUrl;

  /** Produit actif = visible dans le catalogue. Archivé = historique conservé. */
  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  /**
   * Statut du produit. PENDING = créé à la volée par un employé, en attente de validation. APPROVED
   * = validé, visible dans le catalogue global.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private ProductStatus status = ProductStatus.APPROVED;

  /**
   * Supérette d'origine quand le produit a été créé à la volée par un employé. Null pour les
   * produits créés par un ADMIN/OWNER directement dans le catalogue.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_store_id")
  private Store createdByStore;

  // ── Relations ─────────────────────────────────────────────────────────

  /** Codes-barres associés à ce produit (EAN officiels ou codes internes générés) */
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Barcode> barcodes = new ArrayList<>();

  /** Références de ce produit dans les différentes supérettes (avec prix et stock) */
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<StoreProduct> storeProducts = new ArrayList<>();
}
