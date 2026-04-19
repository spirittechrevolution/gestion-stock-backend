package com.africa.samba.codeLists;

/**
 * Types de vente supportés par la caisse Samba POS.
 *
 * <p>COMPTANT → vente directe, paiement immédiat GROS → tarif grossiste, prix différencié DEVIS →
 * bon de commande, stock non décrémenté COMMANDE_DISTANCE→ commande par téléphone/WhatsApp, mise en
 * attente
 */
public enum TypeVente {
  COMPTANT("Vente comptant", true, true),
  GROS("Vente en gros", true, true),
  DEVIS("Devis / bon de commande", false, false),
  COMMANDE_DISTANCE("Commande distance", false, false);

  private final String libelle;

  /** True si la vente décrémente le stock à la validation */
  private final boolean decrementeStock;

  /** True si un reçu est généré automatiquement */
  private final boolean genereRecu;

  TypeVente(String libelle, boolean decrementeStock, boolean genereRecu) {
    this.libelle = libelle;
    this.decrementeStock = decrementeStock;
    this.genereRecu = genereRecu;
  }

  public String getLibelle() {
    return libelle;
  }

  public boolean isDecrementeStock() {
    return decrementeStock;
  }

  public boolean isGenereRecu() {
    return genereRecu;
  }
}
