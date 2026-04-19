package com.africa.samba.codeLists;

/**
 * Statut d'une boutique cliente dans le dashboard admin Samba.
 *
 * <p>EN_ATTENTE → inscription créée, QR onboarding pas encore scanné ACTIVE → boutique
 * opérationnelle, abonnement valide SUSPENDUE → abonnement expiré ou suspendu manuellement RESILIEE
 * → compte fermé définitivement
 */
public enum StatutBoutique {
  EN_ATTENTE("En attente d'activation"),
  ACTIVE("Active"),
  SUSPENDUE("Suspendue"),
  RESILIEE("Résiliée");

  private final String libelle;

  StatutBoutique(String libelle) {
    this.libelle = libelle;
  }

  public String getLibelle() {
    return libelle;
  }

  public boolean isOperationnelle() {
    return this == ACTIVE;
  }
}
