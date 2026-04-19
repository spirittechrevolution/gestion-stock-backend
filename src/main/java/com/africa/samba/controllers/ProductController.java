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

  @Operation(
      summary = "Créer un produit dans le catalogue global",
      description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent créer un produit dans le catalogue global.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Produit créé",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé"),
    @ApiResponse(responseCode = "409", description = "Produit déjà existant")
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

  @Operation(
      summary = "Obtenir un produit par son identifiant",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE, etc.). Toute personne connectée peut consulter un produit.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Produit trouvé",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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

  @Operation(
      summary = "Lister les produits actifs avec pagination",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE, etc.). Toute personne connectée peut lister les produits.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Liste des produits",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
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

  @Operation(
      summary = "Rechercher des produits par mot-clé",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE, etc.). Toute personne connectée peut rechercher des produits.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Résultats de recherche",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
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

  @Operation(
      summary = "Lister les produits par catégorie",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE, etc.). Toute personne connectée peut lister les produits par catégorie.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Produits de la catégorie",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
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

  @Operation(
      summary = "Mettre à jour un produit",
      description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent modifier un produit.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Produit mis à jour",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "400", description = "Données invalides"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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

  @Operation(
      summary = "Désactiver un produit (soft delete)",
      description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent désactiver un produit.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Produit désactivé",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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

  @Operation(
      summary = "Ajouter un code-barres à un produit",
      description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent ajouter un code-barres à un produit.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Code-barres ajouté",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "409", description = "Code-barres déjà existant"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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

  // ── Générer un code-barres interne ────────────────────────────

    @Operation(
      summary = "Générer un code-barres interne pour un produit",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE, etc.). Toute personne connectée peut générer un code-barres interne. Génère un code-barres séquentiel au format 2XXXXXXXXXXXX (préfixe 2 + 12 chiffres, ex : 2000000000001).")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Code-barres interne généré",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
  @PostMapping("/{productId}/barcodes/generate")
  public ResponseEntity<CustomResponse> generateInternalBarcode(
      @PathVariable UUID productId, HttpServletRequest httpRequest) throws CustomException {

    RoleGuard.requireAuthenticated(requestHeaderParser, httpRequest);

    ProductResponse response = productService.generateInternalBarcode(productId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.CREATED,
                ResponseMessageConstants.BARCODE_GENERATE_SUCCESS,
                response));
  }

  // ── Rechercher par code-barres ────────────────────────────────

  @Operation(
      summary = "Rechercher un produit par code-barres",
      description = "Rôle requis : Authentifié (ADMIN, OWNER, EMPLOYEE, etc.). Toute personne connectée peut rechercher un produit par code-barres.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Code-barres trouvé",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.BarcodeResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Code-barres introuvable"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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
      description = "Rôle requis : Authentifié (EMPLOYEE, OWNER, ADMIN, etc.). Un employé ou administrateur peut créer rapidement un produit en statut PENDING pour sa supérette.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Produit créé (en attente de validation)",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Supérette introuvable"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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
      description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent approuver un produit en attente (PENDING → APPROVED).")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Produit approuvé",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "404", description = "Produit introuvable"),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
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

  @Operation(
      summary = "Lister les produits en attente de validation pour une supérette",
      description = "Rôle requis : ADMIN ou OWNER. Seuls les administrateurs ou propriétaires peuvent lister les produits en attente de validation pour une supérette.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Produits en attente",
        content = @Content(
            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProductResponse.class)
        )
    ),
    @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
    @ApiResponse(responseCode = "403", description = "Accès refusé")
  })
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
