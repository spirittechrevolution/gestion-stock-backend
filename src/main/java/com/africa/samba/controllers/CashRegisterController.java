package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.AddCashRegisterRequest;
import com.africa.samba.dto.request.UpdateCashRegisterRequest;
import com.africa.samba.dto.response.CashRegisterResponse;
import com.africa.samba.services.interfaces.CashRegisterService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "Caisses", description = "Gestion des caisses d'une supérette")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;
    private final RequestHeaderParser requestHeaderParser;

    @Operation(
        summary = "Créer une caisse",
        description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent créer une caisse.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Caisse créée",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CashRegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN requis"),
        @ApiResponse(responseCode = "409", description = "Numéro déjà utilisé")
    })
    @PostMapping("/{storeId}/cash-registers")
    public ResponseEntity<CustomResponse> create(
            @PathVariable UUID storeId,
            @Valid @RequestBody AddCashRegisterRequest request,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAdmin(requestHeaderParser, httpRequest);
        CashRegisterResponse response = cashRegisterService.create(storeId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponse(
                        Constants.Message.SUCCESS_BODY,
                        Constants.Status.CREATED,
                        ResponseMessageConstants.CASH_REGISTER_CREATE_SUCCESS,
                        response));
    }

    @Operation(
        summary = "Lister les caisses actives",
        description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retourne la liste paginée des caisses de la supérette.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des caisses",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CashRegisterResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    @GetMapping("/{storeId}/cash-registers")
    public ResponseEntity<CustomResponse> list(
            @PathVariable UUID storeId,
            Pageable pageable,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        Page<CashRegisterResponse> page = cashRegisterService.list(storeId, pageable);
        return ResponseEntity.ok(new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.CASH_REGISTER_GET_LIST_SUCCESS,
            page));
    }

    @Operation(
        summary = "Mettre à jour une caisse",
        description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent modifier une caisse.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caisse mise à jour",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CashRegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Caisse introuvable")
    })
    @PutMapping("/{storeId}/cash-registers/{cashRegisterId}")
    public ResponseEntity<CustomResponse> update(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            @Valid @RequestBody UpdateCashRegisterRequest request,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAdmin(requestHeaderParser, httpRequest);
        CashRegisterResponse response = cashRegisterService.update(storeId, cashRegisterId, request);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                ResponseMessageConstants.CASH_REGISTER_UPDATE_SUCCESS,
                response));
    }

    @Operation(
        summary = "Désactiver une caisse",
        description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent désactiver une caisse.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caisse désactivée"),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Caisse introuvable")
    })
    @DeleteMapping("/{storeId}/cash-registers/{cashRegisterId}")
    public ResponseEntity<CustomResponse> remove(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAdmin(requestHeaderParser, httpRequest);
        cashRegisterService.remove(storeId, cashRegisterId);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                ResponseMessageConstants.CASH_REGISTER_REMOVE_SUCCESS,
                null));
    }
}
