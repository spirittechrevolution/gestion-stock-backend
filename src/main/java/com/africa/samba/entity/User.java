package com.africa.samba.entity;

import com.africa.samba.codeLists.Role;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Utilisateur Samba.
 *
 * <p>Connexion rapide par code PIN 4 chiffres (haché en BCrypt). Session fermée automatiquement
 * après 15 min d'inactivité. Chaque transaction est horodatée et liée à l'utilisateur connecté.
 *
 * <p>Un propriétaire peut gérer plusieurs supérettes ({@link Store}).
 * Les managers et employés sont rattachés à une supérette spécifique.
 */
@Entity
@Table(name = "users", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

  // ── Identité ──────────────────────────────────────────────────────────
  /**
   * Identifiant unique Keycloak (sub du JWT). Non modifiable après création ({@code updatable =
   * false}). Sert de clé de réconciliation entre Keycloak et la base Samba.
   */
  @Column(name = "keycloak_id", nullable = false, updatable = false, length = 100)
  private String keycloakId;

  /**
   * Prénom. Synchronisé depuis le claim Keycloak {@code given_name} au premier login et modifiable
   * par l'utilisateur ensuite.
   */
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  /** Nom de famille. Synchronisé depuis le claim Keycloak {@code family_name}. */
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Email
  @Column(length = 100)
  private String email;

  @Pattern(regexp = "^(\\+?221)?[0-9]{9}$")
  @Column(length = 20)
  private String phone;

  /**
   * Date de naissance. Utilisée pour les vérifications KYC et pour s'assurer que l'utilisateur est
   * majeur avant une transaction.
   */
  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  /**
   * Adresse postale complète (rue, numéro, appartement). Utilisée pour la facturation et les
   * documents légaux.
   */
  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  /** Ville de résidence. */
  @Column(name = "city", length = 100)
  private String city;

  /** Code pays ISO 3166-1 alpha-2 (ex : {@code SN} pour Sénégal). Défaut : {@code SN}. */
  @Column(name = "country", length = 5)
  @Builder.Default
  private String country = "SN";

  /**
   * Code langue ISO 639-1 préféré pour l'interface. Défaut : {@code fr}. Valeurs supportées :
   * {@code fr | en | ar | wo}.
   */
  @Column(name = "language", length = 5)
  @Builder.Default
  private String language = "fr";

  /**
   * URL de l'avatar stocké dans le bucket MinIO {@code users-avatars}. Format : {@code
   * http://minio:9000/users-avatars/{keycloakId}.webp}
   */
  @Column(name = "avatar_url", length = 500)
  private String avatarUrl;

  // ── Préférences personnalisées ────────────────────────────────────────

  /**
   * Préférences UI/UX de l'utilisateur. Créées automatiquement avec des valeurs par défaut à
   * l'inscription. CascadeType.ALL : supprimées avec l'utilisateur.
   */
  @OneToOne(
      mappedBy = "utilisateur",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private UserPreferences preferences;

  // ── Authentification ──────────────────────────────────────────────────
  /** {@code true} si l'email a été confirmé via le lien Keycloak (VERIFY_EMAIL). */
  @Column(name = "is_email_verified", nullable = false)
  @Builder.Default
  private boolean emailVerified = false;

  /** {@code true} si le téléphone a été validé par code OTP SMS. */
  @Column(name = "is_phone_verified", nullable = false)
  @Builder.Default
  private boolean phoneVerified = false;

  /** Hash BCrypt du code PIN 4 chiffres. Jamais stocké en clair. */
  @Column(name = "pin_hash", nullable = false, length = 72)
  private String pinHash;

  /**
   * Token JWT de session en cours (optionnel — pour invalidation). Nullable car l'utilisateur peut
   * être déconnecté.
   */
  @Column(name = "token_session", length = 512)
  private String tokenSession;

  @Column(name = "derniere_connexion")
  private LocalDateTime lastLoginAt;

  /**
   * Date et heure de suppression logique du compte (soft delete). Filtrer les comptes actifs :
   * {@code WHERE deleted_at IS NULL}.
   */
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  // ── Rôle et droits ────────────────────────────────────────────────────

  /**
   * Rôles fonctionnels dupliqués depuis le realm Keycloak. Utilisés pour les requêtes métier
   * (filtrer les biens par agent, etc.). La vérification d'accès réelle reste assurée par Spring
   * Security via le JWT. Valeurs : {@code CLIENT | OWNER | AGENT | AGENCY | ADMIN}
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_roles",
      schema = "administrative",
      joinColumns = @JoinColumn(name = "user_id"),
      foreignKey = @ForeignKey(name = "fk_user_roles_user"),
      indexes = @Index(name = "idx_user_roles_role", columnList = "role"))
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 30)
  @Builder.Default
  private Set<Role> roles = new HashSet<>(Set.of(Role.EMPLOYEE));

  /**
   * {@code false} si le compte a été suspendu par un administrateur. Un compte inactif est refusé
   * au niveau du filtre JWT même si Keycloak délivre un token valide (double vérification
   * applicative).
   */
  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean active = true;

  // ── Supérettes gérées ─────────────────────────────────────────────────

  /** Supérettes dont cet utilisateur est propriétaire */
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @Builder.Default
  private List<Store> stores = new ArrayList<>();

  // ── Méthodes métier ───────────────────────────────────────────────────

  /**
   * Nom complet affiché dans l'interface et les documents générés. Exemple : {@code "Mohamed Ba"}.
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * {@code true} si le profil est entièrement vérifié (email + téléphone + KYC). Condition requise
   * pour autoriser les transactions importantes.
   */
  public boolean isFullyVerified() {
    return emailVerified && phoneVerified;
  }

  /** {@code true} si le compte a été supprimé logiquement. */
  public boolean isDeleted() {
    return deletedAt != null;
  }
}
