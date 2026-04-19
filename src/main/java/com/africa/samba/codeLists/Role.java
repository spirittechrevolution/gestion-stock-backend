package com.africa.samba.codeLists;

/**
 * Rôles utilisateurs dans Samba POS.
 *
 * <p>PROPRIETAIRE → tous les droits boutique, y compris gestion users/settings GERANT → droits
 * opérationnels complets sauf gestion users/settings VENDEUR → ventes uniquement, consultation de
 * son bilan personnel ADMIN_SAMBA → accès dashboard interne Samba (toutes boutiques)
 * SUPER_ADMIN_SAMBA → accès total plateforme, gestion des ADMIN_SAMBA, config système
 */
public enum Role {
  PROPRIETAIRE(true, true, true, true, true, true, true),
  GERANT(true, true, true, true, true, true, false),
  VENDEUR(true, false, false, false, false, false, false),
  ADMIN_SAMBA(true, true, true, true, true, true, true),
  /**
   * Super-admin Samba — accès total à la plateforme, gestion des ADMIN_SAMBA, configuration
   * système, accès à toutes les boutiques sans restriction.
   */
  SUPER_ADMIN_SAMBA(true, true, true, true, true, true, true);

  /** Peut encaisser une vente */
  private final boolean peutVendre;

  /** Peut appliquer une remise */
  private final boolean peutAppliquerRemise;

  /** Peut modifier les prix */
  private final boolean peutModifierPrix;

  /** Peut annuler une vente */
  private final boolean peutAnnulerVente;

  /** Peut ajouter/modifier un produit */
  private final boolean peutGererProduits;

  /** Peut consulter tous les rapports */
  private final boolean peutVoirTousRapports;

  /** Peut gérer les utilisateurs et paramètres boutique */
  private final boolean peutGererUtilisateurs;

  Role(
      boolean peutVendre,
      boolean peutAppliquerRemise,
      boolean peutModifierPrix,
      boolean peutAnnulerVente,
      boolean peutGererProduits,
      boolean peutVoirTousRapports,
      boolean peutGererUtilisateurs) {
    this.peutVendre = peutVendre;
    this.peutAppliquerRemise = peutAppliquerRemise;
    this.peutModifierPrix = peutModifierPrix;
    this.peutAnnulerVente = peutAnnulerVente;
    this.peutGererProduits = peutGererProduits;
    this.peutVoirTousRapports = peutVoirTousRapports;
    this.peutGererUtilisateurs = peutGererUtilisateurs;
  }

  public boolean isPeutVendre() {
    return peutVendre;
  }

  public boolean isPeutAppliquerRemise() {
    return peutAppliquerRemise;
  }

  public boolean isPeutModifierPrix() {
    return peutModifierPrix;
  }

  public boolean isPeutAnnulerVente() {
    return peutAnnulerVente;
  }

  public boolean isPeutGererProduits() {
    return peutGererProduits;
  }

  public boolean isPeutVoirTousRapports() {
    return peutVoirTousRapports;
  }

  public boolean isPeutGererUtilisateurs() {
    return peutGererUtilisateurs;
  }

  /** Compatibilité Spring Security — préfixe SAMBA_ */
  public String getAuthority() {
    return "SAMBA_" + this.name();
  }

  /** True si le rôle appartient à l'équipe interne Samba (pas un client) */
  public boolean isEquipeSamba() {
    return this == ADMIN_SAMBA || this == SUPER_ADMIN_SAMBA;
  }

  /** True si le rôle a tous les droits sur une boutique */
  public boolean isFullAccess() {
    return this == PROPRIETAIRE || this == ADMIN_SAMBA || this == SUPER_ADMIN_SAMBA;
  }
}
