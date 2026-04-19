package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.CascadeType;
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
import lombok.experimental.SuperBuilder;

/**
 * Store — supérette.
 *
 * <p>Chaque supérette appartient à un propriétaire ({@link User}).
 * Elle choisit ses produits dans le catalogue global ({@link Product})
 * et définit ses propres prix et stocks via {@link StoreProduct}.
 *
 * <p>Un propriétaire peut gérer plusieurs supérettes.
 */
@Entity
@Table(name = "stores", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Store extends BaseEntity {

  /** Nom de la supérette (ex : "Superette Dakar Liberté") */
  @NotBlank(message = "Le nom de la supérette est obligatoire")
  @Size(min = 2, max = 100)
  @Column(nullable = false, length = 100)
  private String name;

  /** Adresse complète de la supérette */
  @Size(max = 255)
  private String address;

  /** Numéro de téléphone de la supérette */
  @Column(length = 20)
  private String phone;

  /** Supérette active = opérationnelle. Inactive = fermée/suspendue. */
  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  // ── Relations ─────────────────────────────────────────────────────────

  /** Propriétaire de la supérette */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  /** Catalogue de la supérette (produits avec prix et stock personnalisés) */
  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<StoreProduct> storeProducts = new ArrayList<>();
}
