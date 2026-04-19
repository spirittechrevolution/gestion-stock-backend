package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.CreateSaleRequest;
import com.africa.samba.dto.response.SaleResponse;
import com.africa.samba.dto.response.SalesStatsResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/** Enregistrement des ventes et calcul des statistiques de chiffre d'affaires et de marge. */
public interface SaleService {

  /**
   * Enregistre une vente dans une session de caisse active.
   *
   * <p>Décrémente le stock du produit et persiste la vente avec son total calculé.
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   * @param sessionId      UUID de la session de caisse
   * @param request        données de la vente (storeProductId, quantité, prix unitaire)
   * @param sellerId       UUID du vendeur (extrait du token JWT)
   * @return la vente enregistrée
   */
  SaleResponse create(UUID storeId, UUID cashRegisterId, UUID sessionId,
      CreateSaleRequest request, UUID sellerId);

  /**
   * Retourne toutes les ventes d'une session (sans pagination — usage interne).
   *
   * @param sessionId UUID de la session
   * @return liste de toutes les ventes de la session
   */
  List<SaleResponse> listBySession(UUID sessionId);

  /**
   * Retourne la liste paginée des ventes d'une session, avec validation de la hiérarchie.
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse (vérifie que la session lui appartient)
   * @param sessionId      UUID de la session
   * @param pageable       paramètres de pagination
   * @return page de ventes
   */
  Page<SaleResponse> listBySession(UUID storeId, UUID cashRegisterId, UUID sessionId,
      Pageable pageable);

  /**
   * Calcule les statistiques (CA, nombre de ventes, marge) d'une session.
   *
   * @param sessionId UUID de la session
   * @return statistiques agrégées de la session
   */
  SalesStatsResponse statsBySession(UUID sessionId);

  /**
   * Calcule les statistiques de toutes les sessions d'une caisse.
   *
   * @param cashRegisterId UUID de la caisse
   * @return statistiques agrégées de la caisse
   */
  SalesStatsResponse statsByCashRegister(UUID cashRegisterId);

  /**
   * Calcule les statistiques de toutes les caisses d'une supérette.
   *
   * @param storeId UUID de la supérette
   * @return statistiques agrégées de la supérette
   */
  SalesStatsResponse statsByStore(UUID storeId);

  /**
   * Calcule les statistiques des ventes d'un vendeur dans une supérette.
   *
   * @param storeId  UUID de la supérette
   * @param sellerId UUID du vendeur
   * @return statistiques du vendeur dans la supérette
   */
  SalesStatsResponse statsBySeller(UUID storeId, UUID sellerId);

  /**
   * Calcule les statistiques d'une supérette sur une période donnée.
   *
   * @param storeId     UUID de la supérette
   * @param periodType  granularité : {@code "day"}, {@code "month"} ou {@code "year"}
   * @param periodValue valeur correspondante : {@code "2026-04-19"}, {@code "2026-04"} ou {@code "2026"}
   * @return statistiques de la période
   */
  SalesStatsResponse statsByPeriod(UUID storeId, String periodType, String periodValue);

  /**
   * Calcule les statistiques de vente d'un produit dans une supérette, avec validation d'appartenance.
   *
   * @param storeId        UUID de la supérette (vérifie que le produit lui appartient)
   * @param storeProductId UUID de l'entrée catalogue supérette
   * @return statistiques du produit
   */
  SalesStatsResponse statsByStoreProduct(UUID storeId, UUID storeProductId);

  /**
   * Calcule les statistiques globales de toutes les supérettes sur une période.
   *
   * @param start début de la période (inclus)
   * @param end   fin de la période (exclus)
   * @return statistiques globales sur la période
   */
  SalesStatsResponse statsByPeriodGlobal(LocalDateTime start, LocalDateTime end);
}
