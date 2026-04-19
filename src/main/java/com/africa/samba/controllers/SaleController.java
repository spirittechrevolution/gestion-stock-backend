package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.CreateSaleRequest;
import com.africa.samba.dto.response.SaleResponse;
import com.africa.samba.dto.response.SalesStatsResponse;
import com.africa.samba.services.interfaces.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ventes", description = "Enregistrement et consultation des ventes par session de caisse")
public class SaleController {

    private final SaleService saleService;
    private final RequestHeaderParser requestHeaderParser;

    @Operation(
        summary = "Enregistrer une vente",
        description = "Rôle requis : EMPLOYEE ou ADMIN. Enregistre une vente dans la session de caisse active.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vente enregistrée",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SaleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "409", description = "Stock insuffisant ou session clôturée")
    })
    @PostMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales")
    public ResponseEntity<CustomResponse> create(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody CreateSaleRequest request,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        UUID sellerId = UUID.fromString(requestHeaderParser.extractKeycloakId(httpRequest));
        SaleResponse response = saleService.create(storeId, cashRegisterId, sessionId, request, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponse(
                        Constants.Message.SUCCESS_BODY,
                        Constants.Status.CREATED,
                        ResponseMessageConstants.CASH_REGISTER_SESSION_OPEN_SUCCESS,
                        response));
    }

    @Operation(
        summary = "Lister les ventes de la session",
        description = "Rôle requis : EMPLOYEE ou ADMIN. Retourne la liste paginée des ventes de la session.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des ventes",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SaleResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    @GetMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales")
    public ResponseEntity<CustomResponse> list(
            @PathVariable UUID storeId,
            @PathVariable UUID cashRegisterId,
            @PathVariable UUID sessionId,
            Pageable pageable,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        Page<SaleResponse> page = saleService.listBySession(storeId, cashRegisterId, sessionId, pageable);
        return ResponseEntity.ok(new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.CASH_REGISTER_SESSION_GET_LIST_SUCCESS,
            page));
    }

    @Operation(
        summary = "Statistiques de la session de caisse",
        description = "Rôle requis : EMPLOYEE ou ADMIN. Retourne le chiffre d'affaires, marge et nombre de ventes de la session.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stats de la session",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SalesStatsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    @GetMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales/stats")
    public ResponseEntity<CustomResponse> statsBySession(
            @PathVariable UUID sessionId,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        var stats = saleService.statsBySession(sessionId);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                "SALES_STATS_SESSION_SUCCESS",
                stats));
    }

    @Operation(
        summary = "Statistiques d'un produit du catalogue",
        description = "Rôle requis : EMPLOYEE ou ADMIN. Statistiques de vente pour un produit précis dans une supérette.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stats du produit dans la supérette",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SalesStatsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    @GetMapping("/{storeId}/products/{storeProductId}/stats")
    public ResponseEntity<CustomResponse> statsByStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID storeProductId,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        var stats = saleService.statsByStoreProduct(storeId, storeProductId);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                "SALES_STATS_STORE_PRODUCT_SUCCESS",
                stats));
    }

    @Operation(
        summary = "Statistiques globales sur une période",
        description = "Rôle requis : ADMIN. Statistiques globales sur toutes les ventes de toutes les supérettes sur une période donnée.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stats globales sur la période",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SalesStatsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN requis")
    })
    @GetMapping("/global-stats")
    public ResponseEntity<CustomResponse> statsByPeriodGlobal(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        var stats = saleService.statsByPeriodGlobal(startDate, endDate);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                "SALES_STATS_GLOBAL_PERIOD_SUCCESS",
                stats));
    }
}
