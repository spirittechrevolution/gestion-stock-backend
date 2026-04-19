package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.AssignAdminRoleRequest;
import com.africa.samba.dto.request.CreateAdminRequest;
import com.africa.samba.dto.response.AdminUserResponse;
import com.africa.samba.services.interfaces.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur de gestion des administrateurs internes Samba.
 *
 * <p>Endpoints réservés aux Super Administrateurs pour la création de comptes admin, et aux
 * administrateurs pour la consultation.
 *
 * <ul>
 *   <li>{@code POST /v1/admin/users} – créer un administrateur (SUPER_ADMIN uniquement)
 *   <li>{@code GET /v1/admin/users} – lister les administrateurs (ADMIN ou SUPER_ADMIN)
 * </ul>
 */
@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration Plateforme", description = "Gestion des administrateurs internes Samba")
public class AdminController {

  private final AdminManagementService adminManagementService;
  private final RequestHeaderParser requestHeaderParser;

  // ── Créer un administrateur ────────────────────────────────────

  @Operation(
        summary = "Créer un administrateur Samba",
        description =
          "Crée un compte administrateur interne Samba. "
            + "Déclenche la création Keycloak, l'attribution du rôle (ADMIN ou SUPER_ADMIN), "
            + "la synchronisation en base et l'envoi d'un email de définition du mot de passe. "
            + "Rôle requis : SUPER_ADMIN. Seul un super administrateur peut créer un administrateur.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Administrateur créé avec succès"),
    @ApiResponse(responseCode = "400", description = "Données invalides ou rôle non autorisé"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé – rôle SUPER_ADMIN requis"),
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
  })
  @PostMapping
  public ResponseEntity<CustomResponse> createAdmin(
      @Valid @RequestBody CreateAdminRequest request, HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireSuperAdmin(requestHeaderParser, httpRequest);

    AdminUserResponse response = adminManagementService.createAdmin(request);
    log.info("Administrateur créé : email={}, role={}", request.getEmail(), request.getRole());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                ResponseMessageConstants.ADMIN_CREATE_SUCCESS,
                response));
  }

  // ── Lister les administrateurs ─────────────────────────────────

  @Operation(
      summary = "Lister les administrateurs Samba",
      description = "Retourne la liste de tous les administrateurs actifs de la plateforme. Rôle requis : ADMIN ou SUPER_ADMIN.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Liste des administrateurs"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN requis")
  })
  @GetMapping
  public ResponseEntity<CustomResponse> listAdmins(Pageable pageable, HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    Page<AdminUserResponse> admins = adminManagementService.listAdmins(pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.USER_GET_LIST_SUCCESS,
            admins));
  }

  // ── Affecter un rôle à un administrateur ───────────────────────

  @Operation(
        summary = "Affecter un rôle à un administrateur Samba",
        description =
          "Modifie le rôle d'un administrateur existant (ADMIN ↔ SUPER_ADMIN). "
            + "L'ancien rôle est retiré dans Keycloak avant d'attribuer le nouveau. "
            + "Rôle requis : SUPER_ADMIN. Seul un super administrateur peut affecter un rôle à un administrateur.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Rôle affecté avec succès"),
    @ApiResponse(responseCode = "400", description = "Rôle invalide"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé – rôle SUPER_ADMIN requis"),
    @ApiResponse(responseCode = "404", description = "Administrateur introuvable")
  })
  @PutMapping("/{userId}/role")
  public ResponseEntity<CustomResponse> assignAdminRole(
      @PathVariable UUID userId,
      @Valid @RequestBody AssignAdminRoleRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireSuperAdmin(requestHeaderParser, httpRequest);

    AdminUserResponse response = adminManagementService.assignAdminRole(userId, request.getRole());
    log.info("Rôle affecté à l'administrateur : userId={}, role={}", userId, request.getRole());

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.ADMIN_ROLE_ASSIGN_SUCCESS,
            response));
  }

  // ── Supprimer logiquement un administrateur ────────────────────

  @Operation(
        summary = "Supprimer logiquement un administrateur Samba",
        description =
          "Effectue une suppression logique : désactive le compte Keycloak (enabled=false) "
            + "et positionne deletedAt en base. Le compte n'est pas physiquement supprimé. "
            + "Rôle requis : SUPER_ADMIN. Seul un super administrateur peut supprimer logiquement un administrateur.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Administrateur supprimé logiquement"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé – rôle SUPER_ADMIN requis"),
    @ApiResponse(responseCode = "404", description = "Administrateur introuvable"),
    @ApiResponse(responseCode = "409", description = "Compte déjà supprimé")
  })
  @DeleteMapping("/{userId}")
  public ResponseEntity<CustomResponse> deleteAdmin(
      @PathVariable UUID userId, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireSuperAdmin(requestHeaderParser, httpRequest);

    adminManagementService.deleteAdmin(userId);
    log.info("Administrateur supprimé logiquement : userId={}", userId);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.ADMIN_DELETE_SUCCESS,
            null));
  }
}
