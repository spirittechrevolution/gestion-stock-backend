package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.OpenCashRegisterSessionRequest;
import com.africa.samba.dto.response.CashRegisterSessionResponse;
import com.africa.samba.services.interfaces.CashRegisterSessionService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sessions de caisse", description = "Gestion des sessions de caisse (assignation vendeur)")
public class CashRegisterSessionController {

    private final CashRegisterSessionService sessionService;
    private final RequestHeaderParser requestHeaderParser;

    @Operation(
        summary = "Ouvrir une session de caisse (assigner un vendeur)",
        description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent ouvrir une session de caisse.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Session ouverte",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CashRegisterSessionResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN requis"),
        @ApiResponse(responseCode = "409", description = "Session déjà ouverte ou vendeur déjà assigné")
    })
    @PostMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions")
    public ResponseEntity<CustomResponse> open(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            @Valid @RequestBody OpenCashRegisterSessionRequest request,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAdmin(requestHeaderParser, httpRequest);
        UUID openedById = UUID.fromString(requestHeaderParser.extractKeycloakId(httpRequest));
        CashRegisterSessionResponse response = sessionService.open(storeId, cashRegisterId, request, openedById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponse(
                        Constants.Message.SUCCESS_BODY,
                        Constants.Status.CREATED,
                        ResponseMessageConstants.CASH_REGISTER_SESSION_OPEN_SUCCESS,
                        response));
    }

    @Operation(
        summary = "Clôturer une session de caisse",
        description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Clôture la session et enregistre le montant de fermeture.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session clôturée",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CashRegisterSessionResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "404", description = "Session introuvable")
    })
    @PutMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/close")
    public ResponseEntity<CustomResponse> close(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            @PathVariable UUID sessionId,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        UUID closedById = UUID.fromString(requestHeaderParser.extractKeycloakId(httpRequest));
        CashRegisterSessionResponse response = sessionService.close(storeId, cashRegisterId, sessionId, closedById);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                ResponseMessageConstants.CASH_REGISTER_SESSION_CLOSE_SUCCESS,
                response));
    }

    @Operation(
        summary = "Lister les sessions de caisse",
        description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retourne la liste paginée des sessions d'une caisse.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des sessions",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CashRegisterSessionResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    @GetMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions")
    public ResponseEntity<CustomResponse> list(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            Pageable pageable,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        Page<CashRegisterSessionResponse> page = sessionService.list(storeId, cashRegisterId, pageable);
        return ResponseEntity.ok(new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.CASH_REGISTER_SESSION_GET_LIST_SUCCESS,
            page));
    }
}
