package com.africa.samba.codeLists;

/**
 * Modes de paiement acceptés par Samba POS. Extensible : ajouter FREE_MONEY, CARTE_BANCAIRE etc.
 * sans casser l'existant.
 */
public enum ModePaiement {
  ESPECES("Espèces", false),
  WAVE("Wave", true),
  ORANGE_MONEY("Orange Money", true),
  FREE_MONEY("Free Money", true),
  MIXTE("Mixte", false); // Combinaison espèces + mobile

  private final String libelle;

  /** True si paiement mobile (Wave, Orange…) */
  private final boolean estMobile;

  ModePaiement(String libelle, boolean estMobile) {
    this.libelle = libelle;
    this.estMobile = estMobile;
  }

  public String getLibelle() {
    return libelle;
  }

  public boolean isEstMobile() {
    return estMobile;
  }
}
