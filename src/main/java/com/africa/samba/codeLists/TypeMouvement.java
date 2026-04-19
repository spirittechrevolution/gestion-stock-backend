package com.africa.samba.codeLists;

/**
 * Types de mouvements de stock — traçabilité complète.
 *
 * <p>ENTREE → livraison fournisseur, réapprovisionnement SORTIE_VENTE → décrémentation automatique
 * à chaque vente validée CORRECTION_POS → correction manuelle positive (inventaire) CORRECTION_NEG
 * → correction manuelle négative (casse, vol, erreur) RETOUR_CLIENT → retour produit, stock
 * recrédité (Phase 2) TRANSFERT_IN → réception depuis autre point de vente (Business) TRANSFERT_OUT
 * → envoi vers autre point de vente (Business)
 */
public enum TypeMouvement {
  ENTREE(true, "Entrée de stock"),
  SORTIE_VENTE(false, "Vente"),
  CORRECTION_POS(true, "Correction positive"),
  CORRECTION_NEG(false, "Correction négative"),
  RETOUR_CLIENT(true, "Retour client"),
  TRANSFERT_IN(true, "Transfert reçu"),
  TRANSFERT_OUT(false, "Transfert envoyé");

  /** True = mouvement augmente le stock, False = diminue le stock */
  private final boolean augmente;

  private final String libelle;

  TypeMouvement(boolean augmente, String libelle) {
    this.augmente = augmente;
    this.libelle = libelle;
  }

  public boolean isAugmente() {
    return augmente;
  }

  public String getLibelle() {
    return libelle;
  }

  /** Signe à appliquer sur la quantité lors du calcul de stock */
  public int signe() {
    return augmente ? 1 : -1;
  }
}
