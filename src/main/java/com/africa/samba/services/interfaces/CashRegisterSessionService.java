package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.OpenCashRegisterSessionRequest;
import com.africa.samba.dto.response.CashRegisterSessionResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/** Gestion des sessions de caisse : ouverture, clôture et consultation. */
public interface CashRegisterSessionService {

  /**
   * Ouvre une session de caisse en assignant un vendeur à une caisse.
   *
   * <p>Une seule session peut être active à la fois par caisse (contrainte {@code UNIQUE} sur
   * {@code cash_register_id} sans {@code closed_at}).
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   * @param request        données d'ouverture (userId du vendeur assigné)
   * @param openedById     UUID de l'utilisateur qui ouvre la session
   * @return la session ouverte
   */
  CashRegisterSessionResponse open(UUID storeId, UUID cashRegisterId,
      OpenCashRegisterSessionRequest request, UUID openedById);

  /**
   * Clôture une session de caisse active.
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   * @param sessionId      UUID de la session à clôturer
   * @param closedById     UUID de l'utilisateur qui clôture la session
   * @return la session clôturée avec {@code closedAt} renseigné
   */
  CashRegisterSessionResponse close(UUID storeId, UUID cashRegisterId, UUID sessionId,
      UUID closedById);

  /**
   * Retourne toutes les sessions d'une caisse (sans pagination).
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   * @return liste de toutes les sessions
   */
  List<CashRegisterSessionResponse> list(UUID storeId, UUID cashRegisterId);

  /**
   * Retourne la liste paginée des sessions d'une caisse.
   *
   * @param storeId        UUID de la supérette
   * @param cashRegisterId UUID de la caisse
   * @param pageable       paramètres de pagination
   * @return page de sessions
   */
  Page<CashRegisterSessionResponse> list(UUID storeId, UUID cashRegisterId, Pageable pageable);
}
