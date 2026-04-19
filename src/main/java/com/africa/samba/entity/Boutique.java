package com.africa.samba.entity;

import com.africa.samba.codeLists.Plan;
import com.africa.samba.codeLists.StatutBoutique;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "boutiques", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Boutique extends BaseEntity {
  // ── Informations de base ──────────────────────────────────────────────

  @NotBlank(message = "Le nom de la boutique est obligatoire")
  @Size(min = 2, max = 100)
  @Column(nullable = false, length = 100)
  private String nom;

  @Size(max = 255)
  private String adresse;

  @Size(max = 50)
  private String ville;

  @Pattern(regexp = "^(\\+?221)?[0-9]{9}$", message = "Numéro de téléphone sénégalais invalide")
  @Column(length = 20)
  private String telephone;

  @Email
  @Column(unique = true, length = 100)
  private String email;

  /** URL du logo stocké sur S3 / Cloudinary */
  @Column(name = "logo_url")
  private String logoUrl;

  /** Message personnalisé affiché en pied de reçu */
  @Size(max = 200)
  @Column(name = "message_recu", length = 200)
  private String messageRecu;

  // ── Abonnement ────────────────────────────────────────────────────────

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Plan plan = Plan.STARTER;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private StatutBoutique statut = StatutBoutique.EN_ATTENTE;

  @Column(name = "date_expiration_plan")
  private LocalDate dateExpirationPlan;

  // ── Onboarding QR ────────────────────────────────────────────────────

  /** Token unique généré par le dashboard admin pour l'onboarding mobile */
  @Column(name = "token_activation", unique = true, length = 64)
  private String tokenActivation;

  /** URL ou base64 du QR code d'onboarding */
  @Column(name = "qr_onboarding_url")
  private String qrOnboardingUrl;

  /** Date du premier scan de l'app mobile par cette boutique */
  @Column(name = "date_activation")
  private LocalDateTime dateActivation;

  /** Dernière synchronisation mobile → backend */
  @Column(name = "derniere_sync")
  private LocalDateTime derniereSync;

  // ── Paramètres caisse ─────────────────────────────────────────────────

  /** Devise affichée sur les reçus */
  @Column(length = 10)
  @Builder.Default
  private String devise = "FCFA";

  /** Afficher le nom du vendeur sur le reçu */
  @Column(name = "afficher_vendeur_recu")
  @Builder.Default
  private Boolean afficherVendeurRecu = true;

  /** Format du numéro de vente ex: VTE-{YYYY}-{SEQ} */
  @Column(name = "format_reference_vente", length = 30)
  @Builder.Default
  private String formatReferenceVente = "VTE-{YYYY}-{SEQ}";

  /** Compteur séquentiel pour les références de vente */
  @Column(name = "compteur_vente")
  @Builder.Default
  private Long compteurVente = 0L;

  // ── Relations ─────────────────────────────────────────────────────────

  @OneToMany(mappedBy = "boutique", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<User> utilisateurs = new ArrayList<>();

  @OneToMany(mappedBy = "boutique", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Produit> produits = new ArrayList<>();

  @OneToMany(mappedBy = "boutique", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Vente> ventes = new ArrayList<>();

  @OneToMany(mappedBy = "boutique", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Categorie> categories = new ArrayList<>();

  // ── Méthodes métier ───────────────────────────────────────────────────

  public boolean isActive() {
    return statut == StatutBoutique.ACTIVE
        && (dateExpirationPlan == null || !LocalDate.now().isAfter(dateExpirationPlan));
  }

  public boolean isInactive(int seuilJours) {
    if (derniereSync == null) return true;
    return derniereSync.isBefore(LocalDateTime.now().minusDays(seuilJours));
  }

  public String genererProchainerReference() {
    this.compteurVente++;
    return formatReferenceVente
        .replace("{YYYY}", String.valueOf(java.time.Year.now().getValue()))
        .replace("{SEQ}", String.format("%05d", compteurVente));
  }
}
