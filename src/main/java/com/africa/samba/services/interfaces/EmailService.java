package com.africa.samba.services.interfaces;

/** Service d'envoi d'emails (inscription, bienvenue, notifications). */
public interface EmailService {

  /**
   * Envoie un email de bienvenue après inscription réussie.
   *
   * @param email adresse email du destinataire
   * @param firstName prénom de l'utilisateur
   */
  void sendWelcome(String email, String firstName);
}
