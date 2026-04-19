package com.africa.samba.codeLists;

/**
 * Type de code-barres.
 *
 * <ul>
 *   <li>{@code EAN} — Code-barres officiel du fabricant (standard GS1, 8 ou 13 chiffres)
 *   <li>{@code INTERNAL} — Code généré par Samba pour les produits sans code-barres (format :
 *       préfixe {@code 2} + 12 chiffres, ex : {@code 2000000000001})
 * </ul>
 */
public enum BarcodeType {
  EAN,
  INTERNAL
}
