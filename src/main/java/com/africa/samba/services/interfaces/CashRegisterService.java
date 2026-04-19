package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.AddCashRegisterRequest;
import com.africa.samba.dto.request.UpdateCashRegisterRequest;
import com.africa.samba.dto.response.CashRegisterResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/** Gestion des caisses enregistreuses d'une supérette. */
public interface CashRegisterService {

  /**
   * Crée une caisse pour une supérette.
   *
   * @param storeId UUID de la supérette
   * @param request données de la caisse (numéro, libellé)
   * @return la caisse créée
   */
  CashRegisterResponse create(UUID storeId, AddCashRegisterRequest request);

  /**
   * Retourne toutes les caisses actives d'une supérette (sans pagination).
   *
   * @param storeId UUID de la supérette
   * @return liste des caisses actives
   */
  List<CashRegisterResponse> list(UUID storeId);

  /**
   * Retourne la liste paginée des caisses actives d'une supérette.
   *
   * @param storeId  UUID de la supérette
   * @param pageable paramètres de pagination
   * @return page de caisses
   */
  Page<CashRegisterResponse> list(UUID storeId, Pageable pageable);

  /**
   * Met à jour le numéro ou le libellé d'une caisse.
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   * @param request        nouvelles valeurs
   * @return la caisse mise à jour
   */
  CashRegisterResponse update(UUID storeId, UUID cashRegisterId, UpdateCashRegisterRequest request);

  /**
   * Désactive une caisse (soft delete).
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   */
  void remove(UUID storeId, UUID cashRegisterId);
}
