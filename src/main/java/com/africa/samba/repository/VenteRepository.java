package com.africa.samba.repository;

import com.africa.samba.entity.Vente;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VenteRepository extends JpaRepository<Vente, UUID> {

  Optional<Vente> findByReferenceAndBoutiqueId(String reference, UUID boutiqueId);

  Optional<Vente> findByIdLocalMobileAndBoutiqueId(String idLocal, UUID boutiqueId);

  boolean existsByIdLocalMobileAndBoutiqueId(String idLocal, UUID boutiqueId);

  // ── Ventes du jour ────────────────────────────────────────────────────

  @Query(
      """
        SELECT v FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt >= :debutJour
          AND v.createdAt < :finJour
        ORDER BY v.createdAt DESC
    """)
  List<Vente> findVentesJour(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debutJour") LocalDateTime debutJour,
      @Param("finJour") LocalDateTime finJour);

  /** CA total du jour */
  @Query(
      """
        SELECT COALESCE(SUM(v.total), 0) FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt >= :debutJour
          AND v.createdAt < :finJour
    """)
  BigDecimal sumTotalJour(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debutJour") LocalDateTime debutJour,
      @Param("finJour") LocalDateTime finJour);

  /** Nombre de transactions du jour */
  @Query(
      """
        SELECT COUNT(v) FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt >= :debutJour
          AND v.createdAt < :finJour
    """)
  UUID countVentesJour(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debutJour") LocalDateTime debutJour,
      @Param("finJour") LocalDateTime finJour);

  /** Répartition par mode de paiement pour le jour */
  @Query(
      """
        SELECT v.modePaiement, COUNT(v), COALESCE(SUM(v.total), 0)
        FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt >= :debutJour
          AND v.createdAt < :finJour
        GROUP BY v.modePaiement
    """)
  List<Object[]> repartitionPaiementJour(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debutJour") LocalDateTime debutJour,
      @Param("finJour") LocalDateTime finJour);

  // ── Rapports période ──────────────────────────────────────────────────

  @Query(
      """
        SELECT v FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt BETWEEN :debut AND :fin
        ORDER BY v.createdAt DESC
    """)
  List<Vente> findByPeriode(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debut") LocalDateTime debut,
      @Param("fin") LocalDateTime fin);

  /** CA par période */
  @Query(
      """
        SELECT COALESCE(SUM(v.total), 0) FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt BETWEEN :debut AND :fin
    """)
  BigDecimal sumTotalPeriode(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debut") LocalDateTime debut,
      @Param("fin") LocalDateTime fin);

  /** Top N produits les plus vendus sur une période */
  @Query(
      """
        SELECT lv.produit.id, lv.nomProduitSnapshot, SUM(lv.quantite), SUM(lv.getSousTotal())
        FROM LigneVente lv
        JOIN lv.vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'VALIDEE'
          AND v.createdAt BETWEEN :debut AND :fin
        GROUP BY lv.produit.id, lv.nomProduitSnapshot
        ORDER BY SUM(lv.quantite) DESC
    """)
  List<Object[]> findTopProduits(
      @Param("boutiqueId") UUID boutiqueId,
      @Param("debut") LocalDateTime debut,
      @Param("fin") LocalDateTime fin);

  // ── Vendeur ───────────────────────────────────────────────────────────

  @Query(
      """
        SELECT v FROM Vente v
        WHERE v.vendeur.id = :vendeurId
          AND v.statut = 'VALIDEE'
          AND v.createdAt >= :debutJour
          AND v.createdAt < :finJour
        ORDER BY v.createdAt DESC
    """)
  List<Vente> findByVendeurJour(
      @Param("vendeurId") UUID vendeurId,
      @Param("debutJour") LocalDateTime debutJour,
      @Param("finJour") LocalDateTime finJour);

  // ── Commandes en attente ──────────────────────────────────────────────

  @Query(
      """
        SELECT v FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'EN_ATTENTE'
        ORDER BY v.createdAt ASC
    """)
  List<Vente> findCommandesEnAttente(@Param("boutiqueId") UUID boutiqueId);

  /** Commandes en attente expirées */
  @Query(
      """
        SELECT v FROM Vente v
        WHERE v.boutique.id = :boutiqueId
          AND v.statut = 'EN_ATTENTE'
          AND v.dateExpirationDevis IS NOT NULL
          AND v.dateExpirationDevis < :maintenant
    """)
  List<Vente> findCommandesExpirees(
      @Param("boutiqueId") UUID boutiqueId, @Param("maintenant") LocalDateTime maintenant);

  // ── Sync offline ──────────────────────────────────────────────────────

  List<Vente> findByBoutiqueIdAndSynchroniseeFalse(UUID boutiqueId);
}
