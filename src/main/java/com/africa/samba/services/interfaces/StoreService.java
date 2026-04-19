package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.CreateStoreRequest;
import com.africa.samba.dto.request.UpdateStoreRequest;
import com.africa.samba.dto.response.StoreResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Gestion des supérettes : création, consultation et cycle de vie. */
public interface StoreService {

  /**
   * Crée une supérette et l'associe à son propriétaire.
   *
   * @param ownerId UUID de l'utilisateur propriétaire
   * @param request données de la supérette (nom, adresse, téléphone)
   * @return la supérette créée
   * @throws CustomException 409 si le propriétaire possède déjà une supérette avec ce nom
   */
  StoreResponse create(UUID ownerId, CreateStoreRequest request) throws CustomException;

  /**
   * Retourne une supérette par son identifiant.
   *
   * @param id UUID de la supérette
   * @return la supérette trouvée
   * @throws CustomException 404 si la supérette est introuvable
   */
  StoreResponse getById(UUID id) throws CustomException;

  /**
   * Retourne la liste paginée des supérettes d'un propriétaire.
   *
   * @param ownerId UUID du propriétaire
   * @param pageable paramètres de pagination et de tri
   * @return page de supérettes du propriétaire
   */
  Page<StoreResponse> listByOwner(UUID ownerId, Pageable pageable);

  /**
   * Retourne la liste paginée de toutes les supérettes de la plateforme (admin).
   *
   * @param pageable paramètres de pagination et de tri
   * @return page de toutes les supérettes
   */
  Page<StoreResponse> listAll(Pageable pageable);

  /**
   * Met à jour les informations d'une supérette.
   *
   * @param id UUID de la supérette
   * @param request nouvelles valeurs (nom, adresse, téléphone)
   * @return la supérette mise à jour
   * @throws CustomException 404 si la supérette est introuvable
   */
  StoreResponse update(UUID id, UpdateStoreRequest request) throws CustomException;

  /**
   * Désactive une supérette (soft delete).
   *
   * @param id UUID de la supérette
   * @throws CustomException 404 si la supérette est introuvable
   */
  void delete(UUID id) throws CustomException;
}
