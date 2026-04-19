package com.africa.samba.repository;

import com.africa.samba.entity.SyncQueue;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncQueueRepository extends JpaRepository<SyncQueue, UUID> {

  /** Opérations non encore traitées d'une boutique — triées par date */
  List<SyncQueue> findByBoutiqueIdAndTraiteeFalseOrderByCreatedAtAsc(UUID boutiqueId);

  /** Opérations en échec pouvant être retentées */
  @Query(
      """
        SELECT s FROM SyncQueue s
        WHERE s.boutique.id = :boutiqueId
          AND s.traitee = false
          AND s.nbTentatives < :maxTentatives
        ORDER BY s.createdAt ASC
    """)
  List<SyncQueue> findARetraiter(
      @Param("boutiqueId") UUID boutiqueId, @Param("maxTentatives") int maxTentatives);

  Optional<SyncQueue> findByIdLocalAndBoutiqueId(String idLocal, UUID boutiqueId);

  boolean existsByIdLocalAndBoutiqueId(String idLocal, UUID boutiqueId);

  /** Nettoyage des entrées traitées (schedulé) */
  @Modifying
  @Query("DELETE FROM SyncQueue s WHERE s.traitee = true AND s.boutique.id = :boutiqueId")
  void deleteTraiteesByBoutique(@Param("boutiqueId") UUID boutiqueId);

  UUID countByBoutiqueIdAndTraiteeFalse(UUID boutiqueId);
}
