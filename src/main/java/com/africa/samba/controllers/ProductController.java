package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.common.util.RequestHeaderParser;
import com.africa.samba.common.util.RoleGuard;
import com.africa.samba.dto.request.CreateBarcodeRequest;
import com.africa.samba.dto.request.CreateProductRequest;
import com.africa.samba.dto.request.QuickCreateProductRequest;
import com.africa.samba.dto.request.UpdateProductRequest;
import com.africa.samba.dto.response.ProductResponse;
import com.africa.samba.services.interfaces.ProductService;
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
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Produits", description = "Catalogue global de produits")
public class ProductController {

  private final ProductService productService;
  private final RequestHeaderParser requestHeaderParser;

  // ── Créer un produit ──────────────────────────────────────────

  @Operation(summary = "Créer un produit dans le catalogue global")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Produit créé"),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
  @PostMapping
  public ResponseEntity<CustomResponse> create(
      @Valid @RequestBody CreateProductRequest request, HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    ProductResponse response = productService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.PRODUCT_CREATE_SUCCESS,
                response));
  }

  // ── Obtenir un produit par ID ─────────────────────────────────

  @Operation(summary = "Obtenir un produit par son identifiant")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit trouvé"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable")
  })
  @GetMapping("/{id}")
  public ResponseEntity<CustomResponse> getById(
      @PathVariable UUID id, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    ProductResponse response = productService.getById(id);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_GET_SUCCESS,
            response));
  }

  // ── Lister les produits (paginé) ──────────────────────────────

  @Operation(summary = "Lister les produits actifs avec pagination")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Liste des produits")})
  @GetMapping
  public ResponseEntity<CustomResponse> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
    Page<ProductResponse> products = productService.list(pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_GET_LIST_SUCCESS,
            products));
  }

  // ── Rechercher des produits ───────────────────────────────────

  @Operation(summary = "Rechercher des produits par mot-clé")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Résultats de recherche")})
  @GetMapping("/search")
  public ResponseEntity<CustomResponse> search(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    Pageable pageable = PageRequest.of(page, size);
    Page<ProductResponse> results = productService.search(keyword, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_GET_LIST_SUCCESS,
            results));
  }

  // ── Lister par catégorie ──────────────────────────────────────

  @Operation(summary = "Lister les produits par catégorie")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Produits de la catégorie")})
  @GetMapping("/category/{category}")
  public ResponseEntity<CustomResponse> listByCategory(
      @PathVariable String category,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    Pageable pageable = PageRequest.of(page, size);
    Page<ProductResponse> products = productService.listByCategory(category, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_GET_LIST_SUCCESS,
            products));
  }

  // ── Mettre à jour un produit ──────────────────────────────────

  @Operation(summary = "Mettre à jour un produit")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit mis à jour"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable")
  })
  @PutMapping("/{id}")
  public ResponseEntity<CustomResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateProductRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    ProductResponse response = productService.update(id, request);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_UPDATE_SUCCESS,
            response));
  }

  // ── Supprimer (soft delete) un produit ────────────────────────

  @Operation(summary = "Désactiver un produit (soft delete)")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit désactivé"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<CustomResponse> delete(
      @PathVariable UUID id, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    productService.delete(id);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_DELETE_SUCCESS,
            null));
  }

  // ── Ajouter un code-barres à un produit ───────────────────────

  @Operation(summary = "Ajouter un code-barres à un produit")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Code-barres ajouté"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "409", description = "Code-barres déjà existant")
  })
  @PostMapping("/{productId}/barcodes")
  public ResponseEntity<CustomResponse> addBarcode(
      @PathVariable UUID productId,
      @Valid @RequestBody CreateBarcodeRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    ProductResponse response = productService.addBarcode(productId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.BARCODE_CREATE_SUCCESS,
                response));
  }

  // ── Rechercher par code-barres ────────────────────────────────

  @Operation(summary = "Rechercher un produit par code-barres")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Code-barres trouvé"),
    @ApiResponse(responseCode = "404", description = "Code-barres introuvable")
  })
  @GetMapping("/barcodes/{code}")
  public ResponseEntity<CustomResponse> lookupBarcode(
      @PathVariable String code, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    ProductResponse.BarcodeResponse response = productService.lookupBarcode(code);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.BARCODE_LOOKUP_SUCCESS,
            response));
  }

  // ── Création rapide par un employé ────────────────────────────

  @Operation(
      summary = "Créer rapidement un produit (employé)",
      description = "Crée un produit en statut PENDING et l'ajoute au catalogue de la supérette.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Produit créé (en attente de validation)"),
    @ApiResponse(responseCode = "404", description = "Supérette introuvable")
  })
  @PostMapping("/stores/{storeId}/quick-create")
  public ResponseEntity<CustomResponse> quickCreate(
      @PathVariable UUID storeId,
      @Valid @RequestBody QuickCreateProductRequest request,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    ProductResponse response = productService.quickCreate(storeId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.PRODUCT_QUICK_CREATE_SUCCESS,
                response));
  }

  // ── Approuver un produit ──────────────────────────────────────

  @Operation(
      summary = "Approuver un produit en attente",
      description = "Passe le statut d'un produit de PENDING à APPROVED.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Produit approuvé"),
    @ApiResponse(responseCode = "404", description = "Produit introuvable")
  })
  @PutMapping("/{id}/approve")
  public ResponseEntity<CustomResponse> approve(
      @PathVariable UUID id, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAdmin(requestHeaderParser, httpRequest);

    ProductResponse response = productService.approve(id);
    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_APPROVE_SUCCESS,
            response));
  }

  // ── Lister les produits en attente d'une supérette ────────────

  @Operation(summary = "Lister les produits en attente de validation pour une supérette")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Produits en attente")})
  @GetMapping("/stores/{storeId}/pending")
  public ResponseEntity<CustomResponse> listPending(
      @PathVariable UUID storeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest httpRequest)
      throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    Pageable pageable = PageRequest.of(page, size);
    Page<ProductResponse> products = productService.listPending(storeId, pageable);

    return ResponseEntity.ok(
        new CustomResponse(
            Constants.Message.SUCCESS_BODY,
            Constants.Status.OK,
            ResponseMessageConstants.PRODUCT_GET_LIST_SUCCESS,
            products));
  }
}
