package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.AddStoreProductRequest;
import com.africa.samba.dto.request.UpdateStoreProductRequest;
import com.africa.samba.dto.response.StoreProductResponse;
import com.africa.samba.services.interfaces.StoreProductService;
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
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Catalogue Supérette", description = "Gestion des produits d'une supérette")
public class StoreProductController {

  private final StoreProductService storeProductService;
  private final RequestHeaderParser requestHeaderParser;

  // ── Ajouter un produit au catalogue ───────────────────────────

  @Operation(summary = "Ajouter un produit au catalogue de la supérette",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Associe un produit du catalogue global à la supérette avec son prix et stock.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Produit ajouté au catalogue",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreProductResponse.class))),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Supérette ou produit introuvable"),
    @ApiResponse(responseCode = "409", description = "Produit déjà dans le catalogue")
  })
  @PostMapping("/{storeId}/products")
  public ResponseEntity<CustomResponse> add(
      @PathVariable UUID storeId,
      @Valid @RequestBody AddStoreProductRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreProductResponse response = storeProductService.add(storeId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.STORE_PRODUCT_ADD_SUCCESS,
                response));
  }

  // ── Lister le catalogue (paginé) ─────────────────────────────

  @Operation(summary = "Lister les produits de la supérette avec pagination",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retourne le catalogue paginé de la supérette.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Catalogue de la supérette",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreProductResponse.class))),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @GetMapping("/{storeId}/products")
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
    Page<StoreProductResponse> products = storeProductService.listByStore(storeId, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_PRODUCT_GET_LIST_SUCCESS,
            products));
  }

  // ── Scanner un code-barres ────────────────────────────────────

  @Operation(summary = "Rechercher un produit par code-barres dans la supérette",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retourne le produit avec le prix de la supérette.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit trouvé",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreProductResponse.class))),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Produit non trouvé pour ce code-barres")
  })
  @GetMapping("/{storeId}/products/scan/{barcode}")
  public ResponseEntity<CustomResponse> scan(
      @PathVariable UUID storeId, @PathVariable String barcode, HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreProductResponse response = storeProductService.scan(storeId, barcode);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_PRODUCT_SCAN_SUCCESS,
            response));
  }

  // ── Alertes stock bas ─────────────────────────────────────────

  @Operation(summary = "Lister les produits en stock bas dans la supérette",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retourne les produits dont le stock est inférieur ou égal au stock_min.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produits en stock bas",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreProductResponse.class))),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
  })
  @GetMapping("/{storeId}/products/low-stock")
  public ResponseEntity<CustomResponse> lowStock(
      @PathVariable UUID storeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    Pageable pageable = PageRequest.of(page, size);
    Page<StoreProductResponse> results = storeProductService.lowStock(storeId, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_PRODUCT_LOW_STOCK_SUCCESS,
            results));
  }

  // ── Mettre à jour un produit du catalogue ─────────────────────

  @Operation(summary = "Modifier le prix / stock d'un produit dans la supérette",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Modifie le prix de vente, prix d'achat ou stock.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit mis à jour",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = StoreProductResponse.class))),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable dans cette supérette")
  })
  @PutMapping("/{storeId}/products/{storeProductId}")
  public ResponseEntity<CustomResponse> update(
      @PathVariable UUID storeId,
      @PathVariable UUID storeProductId,
      @Valid @RequestBody UpdateStoreProductRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    StoreProductResponse response = storeProductService.update(storeId, storeProductId, request);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_PRODUCT_UPDATE_SUCCESS,
            response));
  }

  // ── Retirer un produit du catalogue ───────────────────────────

  @Operation(summary = "Retirer un produit du catalogue de la supérette (soft delete)",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE). Retire le produit du catalogue sans le supprimer du catalogue global.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit retiré du catalogue"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable dans cette supérette")
  })
  @DeleteMapping("/{storeId}/products/{storeProductId}")
  public ResponseEntity<CustomResponse> remove(
      @PathVariable UUID storeId, @PathVariable UUID storeProductId, HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    storeProductService.remove(storeId, storeProductId);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.STORE_PRODUCT_REMOVE_SUCCESS,
            null));
  }
}
