package com.africa.samba.common.util;

/**
 * Générateur de codes-barres internes Samba.
 *
 * <p>Format : préfixe {@code 2} suivi de 12 chiffres séquentiels.
 *
 * <ul>
 *   <li>Premier code : {@code 2000000000001}
 *   <li>Deuxième code : {@code 2000000000002}
 *   <li>Dernier code possible : {@code 2999999999999}
 * </ul>
 */
public final class BarcodeGenerator {

  /** Premier code interne de la séquence. */
  public static final String FIRST_CODE = "2000000000001";

  private static final long PREFIX_OFFSET = 2_000_000_000_000L;
  private static final long MAX_CODE = 2_999_999_999_999L;

  private BarcodeGenerator() {}

  /**
   * Calcule le prochain code interne à partir du dernier code attribué.
   *
   * @param lastCode le dernier code interne en base ({@code null} si aucun code n'existe encore)
   * @return le prochain code au format {@code 2XXXXXXXXXXXX}
   * @throws IllegalStateException si la séquence est épuisée
   */
  public static String next(String lastCode) {
    if (lastCode == null || lastCode.isBlank()) {
      return FIRST_CODE;
    }

    long current = Long.parseLong(lastCode);
    long next = current + 1;

    if (next > MAX_CODE) {
      throw new IllegalStateException(
          "Séquence de codes-barres internes épuisée (max = " + MAX_CODE + ")");
    }

    return String.valueOf(next);
  }
}
