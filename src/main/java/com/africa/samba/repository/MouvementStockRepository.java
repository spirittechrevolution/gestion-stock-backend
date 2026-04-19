package com.africa.samba.repository;

import com.africa.samba.codeLists.TypeMouvement;
import com.africa.samba.entity.MouvementStock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, UUID> {

  List<MouvementStock> findByProduitIdOrderByCreatedAtDesc(UUID produitId);

  List<MouvementStock> findByProduitIdAndTypeMouvement(UUID produitId, TypeMouvement type);

  /** Historique des mouvements d'un produit sur une période */
  @Query(
      """
        SELECT m FROM MouvementStock m
        WHERE m.produit.id = :produitId
          AND m.createdAt BETWEEN :debut AND :fin
        ORDER BY m.createdAt DESC
    """)
  List<MouvementStock> findByProduitAndPeriode(
      @Param("produitId") UUID produitId,
      @Param("debut") LocalDateTime debut,
      @Param("fin") LocalDateTime fin);

  /** Tous les mouvements d'une boutique sur une période — pour audit */
  @Query(
      """
        SELECT m FROM MouvementStock m
        WHERE m.produit.boutique.id = :boutiqueId
          AND m.createdAt BETWEEN :debut AND :fin
        ORDER BY m.createdAt DESC
    """)
  List<MouvementStock> findByBoutiqueAndPeriode(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debut") LocalDateTime debut,
      @Param("fin") LocalDateTime fin);
}
