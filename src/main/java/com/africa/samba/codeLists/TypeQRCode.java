package com.africa.samba.codeLists;

/**
 * Types de QR codes générés par le backend Samba.
 *
 * <p>PRODUIT → collé sur l'étiquette du produit, scanné à la caisse BOUTIQUE → envoyé au client
 * pour l'onboarding de l'app mobile
 */
public enum TypeQRCode {
  PRODUIT("Étiquette produit", "api/products/%s/qrcode", 300, 300),
  BOUTIQUE("Onboarding boutique", "api/admin/boutiques/%s/qr", 400, 400);

  private final String libelle;
  private final String endpointPattern;

  /** Dimensions PNG générées par ZXing */
  private final int largeurPx;

  private final int hauteurPx;

  TypeQRCode(String libelle, String endpointPattern, int largeurPx, int hauteurPx) {
    this.libelle = libelle;
    this.endpointPattern = endpointPattern;
    this.largeurPx = largeurPx;
    this.hauteurPx = hauteurPx;
  }

  public String getLibelle() {
    return libelle;
  }

  public String getEndpointPattern() {
    return endpointPattern;
  }

  public int getLargeurPx() {
    return largeurPx;
  }

  public int getHauteurPx() {
    return hauteurPx;
  }
}
