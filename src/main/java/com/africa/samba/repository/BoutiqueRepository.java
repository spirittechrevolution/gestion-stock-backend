package com.africa.samba.repository;

import com.africa.samba.codeLists.Plan;
import com.africa.samba.codeLists.StatutBoutique;
import com.africa.samba.entity.Boutique;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoutiqueRepository extends JpaRepository<Boutique, UUID> {

  Optional<Boutique> findByEmail(String email);

  Optional<Boutique> findByTokenActivation(String token);

  boolean existsByEmail(String email);

  List<Boutique> findByStatut(StatutBoutique statut);

  List<Boutique> findByPlan(Plan plan);

  /** Boutiques inactives depuis plus de X jours — pour alertes dashboard admin */
  @Query(
      """
        SELECT b FROM Boutique b
        WHERE b.statut = 'ACTIVE'
          AND (b.derniereSync IS NULL OR b.derniereSync < :seuil)
        ORDER BY b.derniereSync ASC NULLS FIRST
    """)
  List<Boutique> findInactivesSince(@Param("seuil") LocalDateTime seuil);

  /** Boutiques avec abonnement expiré */
  @Query(
      """
        SELECT b FROM Boutique b
        WHERE b.statut = 'ACTIVE'
          AND b.dateExpirationPlan IS NOT NULL
          AND b.dateExpirationPlan < CURRENT_DATE
    """)
  List<Boutique> findAbonnementsExpires();

  /** Mise à jour de la date de dernière sync */
  @Modifying
  @Query("UPDATE Boutique b SET b.derniereSync = :date WHERE b.id = :id")
  void updateDerniereSync(@Param("id") UUID id, @Param("date") LocalDateTime date);

  /** Compte le nombre de boutiques par statut — dashboard admin */
  @Query("SELECT b.statut, COUNT(b) FROM Boutique b GROUP BY b.statut")
  List<Object[]> countByStatut();

  /** Boutiques créées dans un intervalle — dashboard admin */
  @Query(
      """
        SELECT b FROM Boutique b
        WHERE b.createdAt BETWEEN :debut AND :fin
        ORDER BY b.createdAt DESC
    """)
  List<Boutique> findCreatedBetween(
      @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
