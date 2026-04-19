package com.africa.samba.repository;

import com.africa.samba.entity.UserPreferences;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {

  Optional<UserPreferences> findByUtilisateurId(UUID utilisateurId);

  boolean existsByUtilisateurId(UUID utilisateurId);

  /** Mise à jour du token FCM après chaque connexion mobile */
  @Modifying
  @Query(
      """
        UPDATE UserPreferences p
        SET p.fcmToken = :token
        WHERE p.utilisateur.id = :utilisateurId
    """)
  void updateFcmToken(@Param("utilisateurId") UUID utilisateurId, @Param("token") String token);

  /** Tous les tokens FCM actifs d'une boutique — pour notifications push groupées */
  @Query(
      """
        SELECT p.fcmToken FROM UserPreferences p
        WHERE p.utilisateur.boutique.id = :boutiqueId
          AND p.utilisateur.active = true
          AND p.fcmToken IS NOT NULL
          AND p.notifRuptureStock = true
    """)
  List<String> findFcmTokensRuptureStock(@Param("boutiqueId") UUID boutiqueId);

  @Query(
      """
        SELECT p.fcmToken FROM UserPreferences p
        WHERE p.utilisateur.boutique.id = :boutiqueId
          AND p.utilisateur.active = true
          AND p.fcmToken IS NOT NULL
          AND p.notifStockFaible = true
    """)
  List<String> findFcmTokensStockFaible(@Param("boutiqueId") UUID boutiqueId);
}
