package com.africa.samba.entity;

import com.africa.samba.codeLists.StoreMemberRole;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * StoreMember — association entre un utilisateur et une supérette.
 *
 * <p>Permet d'affecter des employés et managers à une supérette spécifique. Le propriétaire (OWNER)
 * est déjà lié via {@link Store#getOwner()}.
 *
 * <p>Contrainte : un utilisateur ne peut être membre qu'une seule fois par supérette.
 */
@Entity
@Table(
    name = "store_members",
    schema = "administrative",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_store_member",
            columnNames = {"store_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StoreMember extends BaseEntity {

  /** Supérette dans laquelle le membre travaille */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  /** Utilisateur membre de la supérette */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** Rôle du membre dans cette supérette (MANAGER ou EMPLOYEE) */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StoreMemberRole role;

  /** Membre actif dans cette supérette */
  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;
}
