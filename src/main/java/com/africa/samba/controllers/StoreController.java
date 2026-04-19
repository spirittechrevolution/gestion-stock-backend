package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.CreateStoreRequest;
import com.africa.samba.dto.request.UpdateStoreRequest;
import com.africa.samba.dto.response.StoreResponse;
import com.africa.samba.entity.User;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.StoreService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supérettes", description = "Gestion des supérettes")
public class StoreController {

  private final StoreService storeService;
  private final UserRepository userRepository;
  private final RequestHeaderParser requestHeaderParser;

  // ── Créer une supérette ───────────────────────────────────────

  @Operation(summary = "Créer une supérette pour l'utilisateur connecté")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Supérette créée"),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "409", description = "Nom déjà utilisé par ce propriétaire")
  })
  @PostMapping
  public ResponseEntity<CustomResponse> create(
      @Valid @RequestBody CreateStoreRequest request, HttpServletRequest httpRequest)
      throws CustomException {

    UUID ownerId = resolveUserId(httpRequest);

    StoreResponse response = storeService.create(ownerId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.STORE_CREATE_SUCCESS,
                response));
  }

  // ── Obtenir une supérette par ID ──────────────────────────────

  @Operation(summary = "Obtenir une supérette par son identifiant")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Supérette trouvée"),
    @ApiResponse(responseCode = "404", description = "Supérette introuvable")
  })
  @GetMapping("/{id}")
  public ResponseEntity<CustomResponse> getById(
      @PathVariable UUID id, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreResponse response = storeService.getById(id);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_GET_SUCCESS,
            response));
  }

  // ── Mes supérettes (propriétaire connecté) ────────────────────

  @Operation(summary = "Lister les supérettes de l'utilisateur connecté")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Liste des supérettes")})
  @GetMapping("/mine")
  public ResponseEntity<CustomResponse> listMine(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      HttpServletRequest httpRequest)
      throws CustomException {

    UUID ownerId = resolveUserId(httpRequest);

    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
    Page<StoreResponse> stores = storeService.listByOwner(ownerId, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_GET_LIST_SUCCESS,
            stores));
  }

  // ── Lister toutes les supérettes (admin) ──────────────────────

  @Operation(summary = "Lister toutes les supérettes (admin)")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Liste de toutes les supérettes"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
  @GetMapping
  public ResponseEntity<CustomResponse> listAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
    Page<StoreResponse> stores = storeService.listAll(pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_GET_LIST_SUCCESS,
            stores));
  }

  // ── Mettre à jour une supérette ───────────────────────────────

  @Operation(summary = "Mettre à jour une supérette")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Supérette mise à jour"),
    @ApiResponse(responseCode = "404", description = "Supérette introuvable")
  })
  @PutMapping("/{id}")
  public ResponseEntity<CustomResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateStoreRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreResponse response = storeService.update(id, request);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_UPDATE_SUCCESS,
            response));
  }

  // ── Supprimer (soft delete) une supérette ─────────────────────

  @Operation(summary = "Désactiver une supérette (soft delete)")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Supérette désactivée"),
    @ApiResponse(responseCode = "404", description = "Supérette introuvable")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<CustomResponse> delete(
      @PathVariable UUID id, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    storeService.delete(id);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_DELETE_SUCCESS,
            null));
  }

  // ── Helper ────────────────────────────────────────────────────
  private UUID resolveUserId(HttpServletRequest httpRequest) {
    String keycloakId = requestHeaderParser.extractKeycloakId(httpRequest);
    User user =
        userRepository
            .findByKeycloakId(keycloakId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Utilisateur introuvable pour le keycloakId : " + keycloakId));
    return user.getId();
  }
}
