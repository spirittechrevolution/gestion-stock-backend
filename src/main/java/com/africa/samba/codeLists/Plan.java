package com.africa.samba.codeLists;

/**
 * Plans d'abonnement Samba POS. Starter → Petite boutique / 1 vendeur Pro → Superette / 3 vendeurs
 * Business→ Supermarché / vendeurs illimités / multi-points de vente
 */
public enum Plan {
  STARTER(5_000, 1, 1, "Petite épicerie, boutique simple"),
  PRO(12_000, 1, 3, "Superette, magasin électronique"),
  BUSINESS(25_000, 3, Integer.MAX_VALUE, "Supermarché, chaîne de boutiques");

  private final int prixMensuelFCFA;
  private final int maxPointsDeVente;
  private final int maxVendeurs;
  private final String description;

  Plan(int prixMensuelFCFA, int maxPointsDeVente, int maxVendeurs, String description) {
    this.prixMensuelFCFA = prixMensuelFCFA;
    this.maxPointsDeVente = maxPointsDeVente;
    this.maxVendeurs = maxVendeurs;
    this.description = description;
  }

  public int getPrixMensuelFCFA() {
    return prixMensuelFCFA;
  }

  public int getMaxPointsDeVente() {
    return maxPointsDeVente;
  }

  public int getMaxVendeurs() {
    return maxVendeurs;
  }

  public String getDescription() {
    return description;
  }

  public boolean isVendeurAutorise(int nbVendeurs) {
    return nbVendeurs <= this.maxVendeurs;
  }

  public boolean isPointDeVenteAutorise(int nb) {
    return nb <= this.maxPointsDeVente;
  }
}
