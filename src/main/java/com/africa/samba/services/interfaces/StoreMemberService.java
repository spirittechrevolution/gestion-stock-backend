package com.africa.samba.services.interfaces;

import com.africa.samba.codeLists.StoreMemberRole;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.AddStoreMemberRequest;
import com.africa.samba.dto.request.UpdateStoreMemberRequest;
import com.africa.samba.dto.response.StoreMemberResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Gestion des membres d'une supérette (employés et managers). */
public interface StoreMemberService {

  /**
   * Ajoute un utilisateur comme membre d'une supérette avec un rôle local.
   *
   * @param storeId UUID de la supérette
   * @param request données du membre (userId, rôle {@code MANAGER} ou {@code EMPLOYEE})
   * @return le membre ajouté
   * @throws CustomException 404 si la supérette ou l'utilisateur est introuvable,
   *                         409 si l'utilisateur est déjà membre de cette supérette
   */
  StoreMemberResponse add(UUID storeId, AddStoreMemberRequest request) throws CustomException;

  /**
   * Met à jour le rôle ou le statut actif d'un membre.
   *
   * @param storeId  UUID de la supérette
   * @param memberId UUID du membre
   * @param request  nouvelles valeurs (rôle, statut actif)
   * @return le membre mis à jour
   * @throws CustomException 404 si le membre est introuvable dans cette supérette
   */
  StoreMemberResponse update(UUID storeId, UUID memberId, UpdateStoreMemberRequest request)
      throws CustomException;

  /**
   * Désactive un membre de la supérette (soft delete — {@code active = false}).
   *
   * @param storeId  UUID de la supérette
   * @param memberId UUID du membre
   * @throws CustomException 404 si le membre est introuvable dans cette supérette
   */
  void remove(UUID storeId, UUID memberId) throws CustomException;

  /**
   * Retourne la liste paginée des membres actifs d'une supérette.
   *
   * @param storeId  UUID de la supérette
   * @param pageable paramètres de pagination et de tri
   * @return page de membres actifs
   */
  Page<StoreMemberResponse> listByStore(UUID storeId, Pageable pageable);

  /**
   * Retourne la liste paginée des membres actifs d'une supérette filtrée par rôle.
   *
   * @param storeId  UUID de la supérette
   * @param role     rôle à filtrer ({@code MANAGER} ou {@code EMPLOYEE})
   * @param pageable paramètres de pagination et de tri
   * @return page de membres actifs du rôle donné
   */
  Page<StoreMemberResponse> listByRole(UUID storeId, StoreMemberRole role, Pageable pageable);

  /**
   * Retourne toutes les supérettes dont un utilisateur est membre actif.
   *
   * @param userId UUID de l'utilisateur
   * @return liste des membres actifs de cet utilisateur (une entrée par supérette)
   */
  List<StoreMemberResponse> listByUser(UUID userId);

  /**
   * Vérifie si un utilisateur est membre actif d'une supérette.
   *
   * @param storeId UUID de la supérette
   * @param userId  UUID de l'utilisateur
   * @return {@code true} si l'utilisateur est membre actif, {@code false} sinon
   */
  boolean isMember(UUID storeId, UUID userId);
}
