package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.UserPreferencesRequest;
import com.africa.samba.dto.response.UserPreferencesResponse;
import java.util.UUID;

/** Gestion des préférences personnalisées de l'utilisateur connecté. */
public interface UserPreferencesService {

  /**
   * Retourne les préférences de l'utilisateur. Les crée avec les valeurs par défaut si elles n'existent pas encore.
   *
   * @param userId UUID interne de l'utilisateur (DB)
   * @return préférences courantes
   */
  UserPreferencesResponse getMyPreferences(UUID userId);

  /**
   * Met à jour les préférences de l'utilisateur. Seuls les champs non-null de la requête sont appliqués.
   *
   * @param userId  UUID interne de l'utilisateur (DB)
   * @param request nouvelles valeurs (champs null ignorés)
   * @return préférences mises à jour
   */
  UserPreferencesResponse updateMyPreferences(UUID userId, UserPreferencesRequest request);

  /**
   * Met à jour uniquement le token FCM. Appelé à chaque démarrage de l'app mobile.
   *
   * @param userId   UUID interne de l'utilisateur (DB)
   * @param fcmToken nouveau token FCM (ou null pour le supprimer)
   */
  void updateFcmToken(UUID userId, String fcmToken);

  /**
   * Réinitialise toutes les préférences aux valeurs par défaut usine.
   *
   * @param userId UUID interne de l'utilisateur (DB)
   * @return préférences après réinitialisation
   */
  UserPreferencesResponse resetToDefaults(UUID userId);
}
