package com.africa.samba.repository;

import com.africa.samba.entity.Produit;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, UUID> {

  // ── Recherches de base ────────────────────────────────────────────────

  List<Produit> findByBoutiqueIdAndActifTrue(UUID boutiqueId);

  Optional<Produit> findByCodeQrAndBoutiqueId(String codeQr, UUID boutiqueId);

  Optional<Produit> findByCodeBarresFabricantAndBoutiqueId(String code, UUID boutiqueId);

  Optional<Produit> findByIdAndBoutiqueId(UUID id, UUID boutiqueId);

  boolean existsByCodeQr(String codeQr);

  boolean existsByCodeBarresFabricantAndBoutiqueId(String code, UUID boutiqueId);

  // ── Recherche texte (caisse + catalogue) ──────────────────────────────

  /**
   * Recherche tolérante par nom — utilisée par la barre de recherche caisse. ILIKE = insensible à
   * la casse (PostgreSQL).
   */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND (LOWER(p.nom) LIKE LOWER(CONCAT('%', :terme, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :terme, '%')))
        ORDER BY p.nom ASC
    """)
  List<Produit> searchByNom(@Param("boutiqueId") UUID boutiqueId, @Param("terme") String terme);

  /** Recherche par catégorie */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.categorie.id = :categorieId
          AND p.actif = true
        ORDER BY p.nom ASC
    """)
  List<Produit> findByCategorie(
      @Param("boutiqueId") UUID boutiqueId, @Param("categorieId") UUID categorieId);

  // ── Alertes stock ─────────────────────────────────────────────────────

  /** Produits en rupture de stock (stockActuel = 0) */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND p.stockActuel <= 0
        ORDER BY p.nom ASC
    """)
  List<Produit> findEnRupture(@Param("boutiqueId") UUID boutiqueId);

  /** Produits sous le seuil minimum (stock faible, non rupture) */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND p.stockActuel > 0
          AND p.stockMinimum IS NOT NULL
          AND p.stockActuel <= p.stockMinimum
        ORDER BY p.stockActuel ASC
    """)
  List<Produit> findStockFaible(@Param("boutiqueId") UUID boutiqueId);

  /** Tous les produits avec alerte (rupture + faible) */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND (p.stockActuel <= 0
            OR (p.stockMinimum IS NOT NULL AND p.stockActuel <= p.stockMinimum))
        ORDER BY p.stockActuel ASC
    """)
  List<Produit> findAllAlertes(@Param("boutiqueId") UUID boutiqueId);

  // ── Péremption ────────────────────────────────────────────────────────

  /** Produits dont la date de péremption est dans les X prochains jours */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND p.datePeremption IS NOT NULL
          AND p.datePeremption BETWEEN CURRENT_DATE AND :dateLimite
        ORDER BY p.datePeremption ASC
    """)
  List<Produit> findProchesPeremption(
      @Param("boutiqueId") UUID boutiqueId, @Param("dateLimite") LocalDate dateLimite);

  /** Produits périmés (date dépassée) */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND p.datePeremption IS NOT NULL
          AND p.datePeremption < CURRENT_DATE
    """)
  List<Produit> findPerimes(@Param("boutiqueId") UUID boutiqueId);

  // ── Rapports ──────────────────────────────────────────────────────────

  /** Produits sans QR code — à générer en batch */
  @Query(
      """
        SELECT p FROM Produit p
        WHERE p.boutique.id = :boutiqueId
          AND p.actif = true
          AND (p.codeQr IS NULL OR p.codeQr = '')
    """)
  List<Produit> findSansQRCode(@Param("boutiqueId") UUID boutiqueId);

  /** Mise à jour directe du QR code (optimisation : évite de charger toute l'entité) */
  @Modifying
  @Query(
      """
        UPDATE Produit p
        SET p.codeQr = :codeQr, p.qrImageUrl = :qrUrl
        WHERE p.id = :id
    """)
  void updateQRCode(
      @Param("id") UUID id, @Param("codeQr") String codeQr, @Param("qrUrl") String qrUrl);

  /** Mise à jour directe du stock (optimisation sync offline) */
  @Modifying
  @Query("UPDATE Produit p SET p.stockActuel = :stock WHERE p.id = :id")
  void updateStock(@Param("id") UUID id, @Param("stock") int stock);

  /** Nombre de produits actifs par boutique */
  UUID countByBoutiqueIdAndActifTrue(UUID boutiqueId);
}
