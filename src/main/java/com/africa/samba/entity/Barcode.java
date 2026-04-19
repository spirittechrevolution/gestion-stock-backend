package com.africa.samba.entity;

import com.africa.samba.codeLists.BarcodeType;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Code-barres — identifiant unique d'un produit.
 *
 * <p>Chaque code est <strong>UNIQUE</strong> dans tout le système.
 * Un produit peut avoir plusieurs codes-barres (EAN officiel + code interne).
 * Un code-barres pointe vers exactement un seul produit.
 *
 * <p>Types :
 * <ul>
 *   <li>{@code EAN} — code officiel du fabricant (standard GS1)</li>
 *   <li>{@code INTERNAL} — code généré par Samba (préfixe {@code 2}, 13 chiffres)</li>
 * </ul>
 */
@Entity
@Table(name = "barcodes", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Barcode extends BaseEntity {

  /** Code-barres unique (EAN-13 ou code interne Samba) */
  @NotBlank(message = "Le code-barres est obligatoire")
  @Size(max = 50)
  @Column(nullable = false, unique = true, length = 50)
  private String code;

  /** Type de code-barres : EAN (officiel) ou INTERNAL (généré par Samba) */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private BarcodeType type = BarcodeType.EAN;

  /** Produit associé à ce code-barres */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
}
