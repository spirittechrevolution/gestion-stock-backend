package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.AddStoreMemberRequest;
import com.africa.samba.dto.request.UpdateStoreMemberRequest;
import com.africa.samba.dto.response.StoreMemberResponse;
import com.africa.samba.services.interfaces.StoreMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stores/{storeId}/members")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Membres Supérette", description = "Gestion des employés et managers d'une supérette")
public class StoreMemberController {

  private final StoreMemberService storeMemberService;
  private final RequestHeaderParser requestHeaderParser;

  // ── Ajouter un membre ─────────────────────────────────────────

  @Operation(summary = "Ajouter un employé ou manager à la supérette",
      description = "Rôle requis : OWNER ou ADMIN. Associe un utilisateur à la supérette avec un rôle local (MANAGER ou EMPLOYEE).")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Membre ajouté",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreMemberResponse.class))),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Supérette ou utilisateur introuvable"),
    @ApiResponse(responseCode = "409", description = "Utilisateur déjà membre")
  })
  @PostMapping("")
  public ResponseEntity<CustomResponse> add(
      @PathVariable UUID storeId,
      @Valid @RequestBody AddStoreMemberRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreMemberResponse response = storeMemberService.add(storeId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.STORE_MEMBER_ADD_SUCCESS,
                response));
  }

  // ── Lister les membres ────────────────────────────────────────

  @Operation(summary = "Lister les membres actifs de la supérette",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retourne la liste paginée des membres actifs.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Liste des membres",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreMemberResponse.class))),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @GetMapping("")
  public ResponseEntity<CustomResponse> list(
      @PathVariable UUID storeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
    Page<StoreMemberResponse> members = storeMemberService.listByStore(storeId, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_MEMBER_GET_LIST_SUCCESS,
            members));
  }

  // ── Modifier le rôle / statut d'un membre ─────────────────────

  @Operation(summary = "Modifier le rôle ou le statut d'un membre",
      description = "Rôle requis : OWNER ou ADMIN. Modifie le rôle (MANAGER/EMPLOYEE) ou le statut actif/inactif d'un membre.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Membre mis à jour",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreMemberResponse.class))),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Membre introuvable")
  })
  @PutMapping("/{memberId}")
  public ResponseEntity<CustomResponse> update(
      @PathVariable UUID storeId,
      @PathVariable UUID memberId,
      @Valid @RequestBody UpdateStoreMemberRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreMemberResponse response = storeMemberService.update(storeId, memberId, request);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_MEMBER_UPDATE_SUCCESS,
            response));
  }

  // ── Retirer un membre ─────────────────────────────────────────

  @Operation(summary = "Retirer un membre de la supérette (soft delete)",
      description = "Rôle requis : OWNER ou ADMIN. Désactive le membre sans le supprimer physiquement.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Membre retiré"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Membre introuvable")
  })
  @DeleteMapping("/{memberId}")
  public ResponseEntity<CustomResponse> remove(
      @PathVariable UUID storeId, @PathVariable UUID memberId, HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    storeMemberService.remove(storeId, memberId);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_MEMBER_REMOVE_SUCCESS,
            null));
  }
}
