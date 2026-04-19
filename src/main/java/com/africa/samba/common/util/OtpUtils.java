package com.africa.samba.common.util;

import java.security.SecureRandom;

/**
 * Utilitaire de génération de codes OTP.
 *
 * <p>Génère des codes numériques à 6 chiffres cryptographiquement sûrs pour les flux d'inscription
 * et de réinitialisation de mot de passe.
 */
public final class OtpUtils {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int OTP_LENGTH = 6;

  private OtpUtils() {}

  /**
   * Génère un code OTP numérique à 6 chiffres.
   *
   * @return code OTP sous forme de chaîne (ex : {@code "042917"})
   */
  public static String generateOtp() {
    int code = RANDOM.nextInt((int) Math.pow(10, OTP_LENGTH));
    return String.format("%0" + OTP_LENGTH + "d", code);
  }
}
