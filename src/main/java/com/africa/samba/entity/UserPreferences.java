package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserPreferences — préférences personnalisées par utilisateur Samba POS.
 *
 * <p>Relation OneToOne avec Utilisateur. Créées automatiquement avec des valeurs par défaut lors de
 * la création d'un utilisateur (via @PostPersist ou le service).
 *
 * <p>Stockées côté backend et synchronisées vers l'app mobile au login. Le vendeur peut modifier
 * ses préférences depuis l'écran Paramètres.
 *
 * <p>Évolutif : de nouvelles préférences peuvent être ajoutées sans casser les utilisateurs
 * existants grâce aux valeurs @Builder.Default.
 */
@Entity
@Table(name = "user_preferences", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences extends BaseEntity {

  // ── Relation utilisateur ──────────────────────────────────────────────

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User utilisateur;

  // ── Interface / Affichage ─────────────────────────────────────────────

  /** Langue de l'interface. "fr" = Français (défaut), "wo" = Wolof */
  @Column(length = 5)
  @Builder.Default
  private String langue = "fr";

  /**
   * Thème de l'application mobile. "light" = clair (défaut), "dark" = sombre, "system" = selon l'OS
   */
  @Column(length = 10)
  @Builder.Default
  private String theme = "light";

  /** Taille de la police dans l'app mobile. "small", "medium" (défaut), "large" */
  @Column(name = "taille_police", length = 10)
  @Builder.Default
  private String taillePolice = "medium";

  // ── Caisse ────────────────────────────────────────────────────────────

  /** Activer le son de confirmation lors d'un scan réussi. */
  @Column(name = "son_scan_actif")
  @Builder.Default
  private Boolean sonScanActif = true;

  /** Activer la vibration lors d'un scan réussi (retour haptique). */
  @Column(name = "vibration_scan_actif")
  @Builder.Default
  private Boolean vibrationScanActif = true;

  /**
   * Mode de paiement par défaut présélectionné à la caisse. Null = aucun présélectionné, le vendeur
   * choisit à chaque fois.
   */
  @Column(name = "mode_paiement_defaut", length = 20)
  private String modePaiementDefaut;

  /**
   * Afficher automatiquement le modal de reçu après chaque vente validée. False = le vendeur doit
   * appuyer sur "Imprimer" manuellement.
   */
  @Column(name = "afficher_recu_auto")
  @Builder.Default
  private Boolean afficherRecuAuto = true;

  /** Mode de partage reçu par défaut. "bluetooth", "whatsapp", "sms", "none" */
  @Column(name = "partage_recu_defaut", length = 15)
  @Builder.Default
  private String partageRecuDefaut = "bluetooth";

  /** Demander confirmation avant de vider le panier. */
  @Column(name = "confirmer_vider_panier")
  @Builder.Default
  private Boolean confirmerViderPanier = true;

  /**
   * Incrémenter automatiquement la quantité au 2e scan du même produit. (comportement décrit dans
   * le MVP : 1er scan = 1, 2e scan = 2)
   */
  @Column(name = "auto_increment_scan")
  @Builder.Default
  private Boolean autoIncrementScan = true;

  // ── Catalogue ─────────────────────────────────────────────────────────

  /** Affichage du catalogue : "grid" (grille, défaut) ou "list" (liste). */
  @Column(name = "vue_catalogue", length = 10)
  @Builder.Default
  private String vueCatalogue = "grid";

  /** Trier les produits par défaut : "nom", "prix", "stock", "recent" */
  @Column(name = "tri_produits_defaut", length = 15)
  @Builder.Default
  private String triProduitsDefaut = "nom";

  /**
   * Afficher les produits en rupture de stock dans le catalogue caisse. False = les produits à
   * stock=0 sont masqués de la caisse.
   */
  @Column(name = "afficher_produits_rupture")
  @Builder.Default
  private Boolean afficherProduitsRupture = false;

  // ── Notifications ─────────────────────────────────────────────────────

  /** Recevoir les notifications push pour les alertes de stock faible. */
  @Column(name = "notif_stock_faible")
  @Builder.Default
  private Boolean notifStockFaible = true;

  /** Recevoir les notifications push pour les ruptures de stock. */
  @Column(name = "notif_rupture_stock")
  @Builder.Default
  private Boolean notifRuptureStock = true;

  /** Recevoir les notifications push pour les produits proches péremption. */
  @Column(name = "notif_peremption")
  @Builder.Default
  private Boolean notifPeremption = true;

  /** Recevoir le résumé journalier automatique (bilan de caisse du soir). */
  @Column(name = "notif_bilan_journalier")
  @Builder.Default
  private Boolean notifBilanJournalier = false;

  /** Heure d'envoi du bilan journalier (format HH:mm, ex: "20:00"). */
  @Column(name = "heure_bilan_journalier", length = 5)
  @Builder.Default
  private String heureBilanJournalier = "20:00";

  // ── Session et sécurité ───────────────────────────────────────────────

  /**
   * Délai d'inactivité avant verrouillage automatique de la session (en minutes). Défaut : 15
   * minutes. Min : 5 min. Max : 60 min.
   */
  @Min(5)
  @Max(60)
  @Column(name = "timeout_session_minutes")
  @Builder.Default
  private Integer timeoutSessionMinutes = 15;

  /** Demander le PIN à chaque ouverture de l'app (même si la session est encore active). */
  @Column(name = "pin_a_chaque_ouverture")
  @Builder.Default
  private Boolean pinAchaqueOuverture = false;

  // ── Dashboard / Rapports ──────────────────────────────────────────────

  /** Période par défaut affichée sur la page de rapports. "jour", "semaine", "mois" */
  @Column(name = "periode_rapport_defaut", length = 10)
  @Builder.Default
  private String periodeRapportDefaut = "jour";

  /**
   * Devise affichée dans les rapports et sur les écrans. Héritée de la boutique mais surchargeable
   * par l'utilisateur.
   */
  @Column(length = 10)
  @Builder.Default
  private String devise = "FCFA";

  /**
   * Token FCM (Firebase Cloud Messaging) pour les notifications push mobile. Mis à jour à chaque
   * connexion depuis l'app mobile.
   */
  @Column(name = "fcm_token", length = 512)
  private String fcmToken;

  // ── Méthodes utilitaires ──────────────────────────────────────────────

  /**
   * Réinitialise toutes les préférences aux valeurs par défaut. Utile si l'utilisateur veut
   * repartir de zéro.
   */
  public void resetToDefaults() {
    this.langue = "fr";
    this.theme = "light";
    this.taillePolice = "medium";
    this.sonScanActif = true;
    this.vibrationScanActif = true;
    this.modePaiementDefaut = null;
    this.afficherRecuAuto = true;
    this.partageRecuDefaut = "bluetooth";
    this.confirmerViderPanier = true;
    this.autoIncrementScan = true;
    this.vueCatalogue = "grid";
    this.triProduitsDefaut = "nom";
    this.afficherProduitsRupture = false;
    this.notifStockFaible = true;
    this.notifRuptureStock = true;
    this.notifPeremption = true;
    this.notifBilanJournalier = false;
    this.heureBilanJournalier = "20:00";
    this.timeoutSessionMinutes = 15;
    this.pinAchaqueOuverture = false;
    this.periodeRapportDefaut = "jour";
  }

  /**
   * Crée des préférences par défaut liées à un utilisateur. Appelé dans le service lors de la
   * création d'un utilisateur.
   */
  public static UserPreferences defautPour(User utilisateur) {
    return UserPreferences.builder()
        .utilisateur(utilisateur)
        .devise(utilisateur.getBoutique() != null ? utilisateur.getBoutique().getDevise() : "FCFA")
        .build();
  }
}
