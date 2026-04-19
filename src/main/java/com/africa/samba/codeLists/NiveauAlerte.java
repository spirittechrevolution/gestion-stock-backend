package com.africa.samba.codeLists;

/**
 * Niveau d'alerte de stock — utilisé pour les badges visuels et notifications push.
 *
 * <p>NORMAL → stock > seuil minimum → barre verte FAIBLE → stock ≤ seuil minimum → badge orange +
 * notification RUPTURE → stock = 0 → badge rouge + notification critique
 */
public enum NiveauAlerte {
  NORMAL("Normal", "green", false),
  FAIBLE("Faible", "orange", true),
  RUPTURE("Rupture", "red", true);

  private final String libelle;
  private final String couleur;

  /** True = envoie une notification push au gérant */
  private final boolean notifie;

  NiveauAlerte(String libelle, String couleur, boolean notifie) {
    this.libelle = libelle;
    this.couleur = couleur;
    this.notifie = notifie;
  }

  public String getLibelle() {
    return libelle;
  }

  public String getCouleur() {
    return couleur;
  }

  public boolean isNotifie() {
    return notifie;
  }

  /** Calcule le niveau d'alerte à partir du stock actuel */
  public static NiveauAlerte calculer(int stockActuel, int stockMinimum) {
    if (stockActuel <= 0) return RUPTURE;
    if (stockActuel <= stockMinimum) return FAIBLE;
    return NORMAL;
  }
}
