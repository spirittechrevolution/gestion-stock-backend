package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.CreateSaleRequest;
import com.africa.samba.dto.response.SaleResponse;
import com.africa.samba.services.interfaces.SaleService;
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
import org.springframework.web.bind.annotation.*;

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
        description = "Rôle requis : EMPLOYEE ou ADMIN. Permet à un vendeur ou un admin d’enregistrer une vente."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vente enregistrée"),
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
        description = "Rôle requis : EMPLOYEE ou ADMIN. Permet à un vendeur ou un admin de consulter les ventes de la session."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Liste des ventes")})
        @GetMapping("/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales")
        public ResponseEntity<CustomResponse> list(
            @PathVariable UUID sessionId,
            Pageable pageable,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        Page<SaleResponse> page = saleService.listBySession(sessionId, pageable);
        return ResponseEntity.ok(new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.CASH_REGISTER_SESSION_GET_LIST_SUCCESS,
            page));
        }

    @Operation(
        summary = "Statistiques de la session de caisse",
        description = "Rôle requis : EMPLOYEE ou ADMIN. Permet à un vendeur ou un admin de consulter les statistiques de la session."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Stats de la session")})
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
    
    /**
     * Statistiques de vente pour un produit précis dans une supérette
     * Rôle requis : EMPLOYEE ou ADMIN
     */
    @Operation(
        summary = "Statistiques d'un produit du catalogue",
        description = "Rôle requis : EMPLOYEE ou ADMIN. Statistiques de vente pour un produit précis dans une supérette."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Stats du produit dans la supérette")})
    @GetMapping("/products/{storeProductId}/stats")
    public ResponseEntity<CustomResponse> statsByStoreProduct(
            @PathVariable UUID storeProductId,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        var stats = saleService.statsByStoreProduct(storeProductId);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                "SALES_STATS_STORE_PRODUCT_SUCCESS",
                stats));
    }

    /**
     * Statistiques globales sur toutes les ventes sur une période donnée
     * Rôle requis : ADMIN uniquement
     */
    @Operation(
        summary = "Statistiques globales sur une période",
        description = "Rôle requis : ADMIN. Statistiques globales sur toutes les ventes de toutes les supérettes sur une période donnée."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Stats globales sur la période")})
    @GetMapping("/global-stats")
    public ResponseEntity<CustomResponse> statsByPeriodGlobal(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            HttpServletRequest httpRequest) throws CustomException {
        RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);
        java.time.LocalDateTime startDate = java.time.LocalDateTime.parse(start);
        java.time.LocalDateTime endDate = java.time.LocalDateTime.parse(end);
        var stats = saleService.statsByPeriodGlobal(startDate, endDate);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                "SALES_STATS_GLOBAL_PERIOD_SUCCESS",
                stats));
    }
}
