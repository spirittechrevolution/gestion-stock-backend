package com.africa.samba.entity;

import com.africa.samba.codeLists.ModePaiement;
import com.africa.samba.codeLists.StatutVente;
import com.africa.samba.codeLists.TypeVente;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vente — transaction commerciale complète.
 *
 * <p>Créée par la caisse mobile (offline-first). Synchronisée avec le backend Spring Boot dès que
 * le réseau est disponible. La référence (ex: VTE-2025-00312) est générée côté mobile et validée
 * backend.
 */
@Entity
@Table(name = "ventes", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente extends BaseEntity {

  // ── Référence ─────────────────────────────────────────────────────────

  /**
   * Référence unique lisible (ex: VTE-2025-00312). Générée côté mobile selon le format configuré
   * sur la boutique. Unique par boutique.
   */
  @Column(name = "reference", nullable = false, length = 30)
  private String reference;

  // ── Classification ────────────────────────────────────────────────────

  @Enumerated(EnumType.STRING)
  @Column(name = "type_vente", nullable = false)
  @Builder.Default
  private TypeVente typeVente = TypeVente.COMPTANT;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private StatutVente statut = StatutVente.VALIDEE;

  // ── Paiement ──────────────────────────────────────────────────────────

  @Enumerated(EnumType.STRING)
  @Column(name = "mode_paiement", nullable = false)
  private ModePaiement modePaiement;

  /** Montant total de la vente en FCFA */
  @NotNull
  @DecimalMin("0.0")
  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal total;

  /** Remise globale sur la vente (montant en FCFA) */
  @DecimalMin("0.0")
  @Column(precision = 12, scale = 2)
  @Builder.Default
  private BigDecimal remise = BigDecimal.ZERO;

  /** Montant reçu du client (pour calcul de la monnaie à rendre) */
  @Column(name = "montant_recu", precision = 14, scale = 2)
  private BigDecimal montantRecu;

  /** Monnaie rendue = montantRecu - total */
  @Column(name = "monnaie_rendue", precision = 14, scale = 2)
  private BigDecimal monnaieRendue;

  // ── Contexte ──────────────────────────────────────────────────────────

  /** Note libre du vendeur sur la vente */
  @Size(max = 300)
  private String note;

  /** Pour COMMANDE_DISTANCE : numéro ou nom du client */
  @Size(max = 100)
  @Column(name = "client_distance")
  private String clientDistance;

  /** Pour DEVIS : date d'expiration du devis */
  @Column(name = "date_expiration_devis")
  private LocalDateTime dateExpirationDevis;

  // ── Synchronisation offline ───────────────────────────────────────────

  /**
   * ID local SQLite généré côté mobile. Permet de détecter les doublons lors de la synchronisation.
   */
  @Column(name = "id_local_mobile", length = 64)
  private String idLocalMobile;

  /** Date de création côté mobile (avant sync) */
  @Column(name = "date_vente_mobile")
  private LocalDateTime dateVenteMobile;

  @Column(name = "synchronisee")
  @Builder.Default
  private Boolean synchronisee = false;

  // ── Relations ─────────────────────────────────────────────────────────

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boutique_id", nullable = false)
  private Boutique boutique;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vendeur_id", nullable = false)
  private User vendeur;

  @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<LigneVente> lignes = new ArrayList<>();

  // ── Méthodes métier ───────────────────────────────────────────────────

  /** Recalcule le total depuis les lignes */
  public BigDecimal calculerTotal() {
    return lignes.stream()
        .map(LigneVente::getSousTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .subtract(remise != null ? remise : BigDecimal.ZERO);
  }

  public void ajouterLigne(LigneVente ligne) {
    ligne.setVente(this);
    this.lignes.add(ligne);
  }

  public void annuler() {
    if (!statut.isModifiable() && statut != StatutVente.VALIDEE)
      throw new IllegalStateException("Cette vente ne peut pas être annulée");
    this.statut = StatutVente.ANNULEE;
  }

  public int getNombreArticles() {
    return lignes.stream().mapToInt(LigneVente::getQuantite).sum();
  }
}
