package com.africa.samba.services.interfaces;

/** Service d'envoi de SMS via LAfricaMobile (LAMPUSH). */
public interface SmsService {

  /**
   * Envoie un code OTP par SMS.
   *
   * @param phone numéro de téléphone destinataire (format international)
   * @param code code OTP à envoyer
   */
  void sendOtp(String phone, String code);
}
