package com.africa.samba.codeLists;

/**
 * Cycle de vie d'une vente dans Samba POS.
 *
 * <p>BROUILLON → panier en cours, non validé (usage futur) EN_ATTENTE → commande à distance /
 * téléphone non encore traitée VALIDEE → vente finalisée, stock décrémenté, reçu généré ANNULEE →
 * annulée par gérant/propriétaire, stock remis à jour REMBOURSEE → retour client, stock recrédité
 * (Phase 2)
 */
public enum StatutVente {
  BROUILLON,
  EN_ATTENTE,
  VALIDEE,
  ANNULEE,
  REMBOURSEE;

  public boolean isTerminee() {
    return this == ANNULEE || this == REMBOURSEE;
  }

  public boolean isModifiable() {
    return this == BROUILLON || this == EN_ATTENTE;
  }
}
