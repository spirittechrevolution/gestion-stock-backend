package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Catégorie de produits — entièrement configurable par le gérant. Ex : Téléphonie, Alimentaire,
 * Hygiène, Électroménager…
 *
 * <p>Chaque boutique possède ses propres catégories. Une catégorie archivée n'est plus proposée à
 * la saisie mais conserve l'historique de ses produits.
 */
@Entity
@Table(name = "categories", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie extends BaseEntity {

  @NotBlank
  @Size(min = 2, max = 80)
  @Column(nullable = false, length = 80)
  private String nom;

  @Size(max = 200)
  private String description;

  /** Couleur hex affichée dans l'app mobile (#1D9E75) */
  @Column(length = 7)
  private String couleur;

  /** Icône identifier (ex: "phone", "food", "health") */
  @Column(length = 30)
  private String icone;

  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boutique_id", nullable = false)
  private Boutique boutique;

  @OneToMany(mappedBy = "categorie")
  @Builder.Default
  private List<Produit> produits = new ArrayList<>();
}
