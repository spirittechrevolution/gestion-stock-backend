package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.FcmTokenUpdateRequest;
import com.africa.samba.dto.request.UserPreferencesRequest;
import com.africa.samba.dto.response.StoreMemberResponse;
import com.africa.samba.dto.response.UserPreferencesResponse;
import com.africa.samba.entity.User;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.StoreMemberService;
import com.africa.samba.services.interfaces.UserPreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateur (moi)", description = "Préférences et supérettes de l'utilisateur connecté")
public class UserPreferencesController {

  private final UserPreferencesService preferencesService;
  private final StoreMemberService storeMemberService;
  private final UserRepository userRepository;
  private final RequestHeaderParser requestHeaderParser;

  // ── Helpers ───────────────────────────────────────────────────────────────

  private User resolveCurrentUser(HttpServletRequest httpRequest) {
    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
    String keycloakId = requestHeaderParser.extractKeycloakId(httpRequest);
    return userRepository
        .findByKeycloakId(keycloakId)
        .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : keycloakId=" + keycloakId));
  }

  // ── GET /v1/users/me/preferences ─────────────────────────────────────────

  @Operation(summary = "Obtenir mes préférences",
      description = "Retourne les préférences de l'utilisateur connecté. Les crée avec les valeurs par défaut si elles n'existent pas encore.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Préférences",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserPreferencesResponse.class))),
      @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @GetMapping("/me/preferences")
  public ResponseEntity<CustomResponse> getMyPreferences(HttpServletRequest httpRequest) {
    User user = resolveCurrentUser(httpRequest);
    UserPreferencesResponse response = preferencesService.getMyPreferences(user.getId());
    return ResponseEntity.ok(new CustomResponse(
        Constants.Message.SUCCESS_BODY,
        Constants.Status.OK,
        ResponseMessageConstants.USER_PREFERENCES_GET_SUCCESS,
        response));
  }

  // ── PUT /v1/users/me/preferences ─────────────────────────────────────────

  @Operation(summary = "Mettre à jour mes préférences",
      description = "Met à jour les préférences de l'utilisateur connecté. Seuls les champs fournis sont appliqués (sémantique PATCH).")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Préférences mises à jour",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserPreferencesResponse.class))),
      @ApiResponse(responseCode = "400", description = "Données invalides"),
      @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @PutMapping("/me/preferences")
  public ResponseEntity<CustomResponse> updateMyPreferences(
      @Valid @RequestBody UserPreferencesRequest request,
      HttpServletRequest httpRequest) {
    User user = resolveCurrentUser(httpRequest);
    UserPreferencesResponse response = preferencesService.updateMyPreferences(user.getId(), request);
    return ResponseEntity.ok(new CustomResponse(
        Constants.Message.SUCCESS_BODY,
        Constants.Status.OK,
        ResponseMessageConstants.USER_PREFERENCES_UPDATE_SUCCESS,
        response));
  }

  // ── PATCH /v1/users/me/preferences/fcm-token ─────────────────────────────

  @Operation(summary = "Mettre à jour le token FCM",
      description = "Met à jour le token Firebase Cloud Messaging pour les notifications push. Appelé au démarrage de l'app mobile.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Token FCM mis à jour"),
      @ApiResponse(responseCode = "400", description = "Token manquant"),
      @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @PatchMapping("/me/preferences/fcm-token")
  public ResponseEntity<CustomResponse> updateFcmToken(
      @Valid @RequestBody FcmTokenUpdateRequest request,
      HttpServletRequest httpRequest) {
    User user = resolveCurrentUser(httpRequest);
    preferencesService.updateFcmToken(user.getId(), request.getFcmToken());
    return ResponseEntity.ok(new CustomResponse(
        Constants.Message.SUCCESS_BODY,
        Constants.Status.OK,
        ResponseMessageConstants.USER_PREFERENCES_FCM_UPDATE_SUCCESS,
        null));
  }

  // ── POST /v1/users/me/preferences/reset ──────────────────────────────────

  @Operation(summary = "Réinitialiser mes préférences",
      description = "Remet toutes les préférences aux valeurs par défaut usine.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Préférences réinitialisées",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserPreferencesResponse.class))),
      @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @PostMapping("/me/preferences/reset")
  public ResponseEntity<CustomResponse> resetToDefaults(HttpServletRequest httpRequest) {
    User user = resolveCurrentUser(httpRequest);
    UserPreferencesResponse response = preferencesService.resetToDefaults(user.getId());
    return ResponseEntity.ok(new CustomResponse(
        Constants.Message.SUCCESS_BODY,
        Constants.Status.OK,
        ResponseMessageConstants.USER_PREFERENCES_RESET_SUCCESS,
        response));
  }

  // ── GET /v1/users/me/stores ───────────────────────────────────────────────

  @Operation(summary = "Lister mes supérettes",
      description = "Retourne toutes les supérettes dont l'utilisateur connecté est membre actif.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Mes supérettes",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = StoreMemberResponse.class))),
      @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @GetMapping("/me/stores")
  public ResponseEntity<CustomResponse> myStores(HttpServletRequest httpRequest) {
    User user = resolveCurrentUser(httpRequest);
    List<StoreMemberResponse> stores = storeMemberService.listByUser(user.getId());
    return ResponseEntity.ok(new CustomResponse(
        Constants.Message.SUCCESS_BODY,
        Constants.Status.OK,
        ResponseMessageConstants.STORE_MEMBER_GET_LIST_SUCCESS,
        stores));
  }
}
