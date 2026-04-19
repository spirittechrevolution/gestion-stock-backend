package com.africa.samba.repository;

import com.africa.samba.entity.Categorie;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, UUID> {

  List<Categorie> findByBoutiqueIdAndActiveTrue(UUID boutiqueId);

  List<Categorie> findByBoutiqueId(UUID boutiqueId);

  Optional<Categorie> findByNomIgnoreCaseAndBoutiqueId(String nom, UUID boutiqueId);

  boolean existsByNomIgnoreCaseAndBoutiqueId(String nom, UUID boutiqueId);

  /** Catégories avec le nombre de produits actifs — pour l'affichage catalogue */
  @Query(
      """
        SELECT c, COUNT(p) FROM Categorie c
        LEFT JOIN c.produits p ON p.actif = true
        WHERE c.boutique.id = :boutiqueId
          AND c.active = true
        GROUP BY c
        ORDER BY c.nom ASC
    """)
  List<Object[]> findWithProductCount(@Param("boutiqueId") UUID boutiqueId);
}
