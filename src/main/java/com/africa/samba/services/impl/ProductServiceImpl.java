package com.africa.samba.services.impl;

import com.africa.samba.codeLists.BarcodeType;
import com.africa.samba.codeLists.ProductStatus;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.ConflictException;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.common.util.BarcodeGenerator;
import com.africa.samba.dto.request.CreateBarcodeRequest;
import com.africa.samba.dto.request.CreateProductRequest;
import com.africa.samba.dto.request.QuickCreateProductRequest;
import com.africa.samba.dto.request.UpdateProductRequest;
import com.africa.samba.dto.response.ProductResponse;
import com.africa.samba.entity.Barcode;
import com.africa.samba.entity.Product;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.StoreProduct;
import com.africa.samba.mapper.ProductMapper;
import com.africa.samba.repository.BarcodeRepository;
import com.africa.samba.repository.ProductRepository;
import com.africa.samba.repository.StoreProductRepository;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.services.interfaces.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final BarcodeRepository barcodeRepository;
  private final StoreRepository storeRepository;
  private final StoreProductRepository storeProductRepository;

  @Override
  @Transactional
  public ProductResponse create(CreateProductRequest request) throws CustomException {
    try {
      Product product =
          Product.builder()
              .name(request.getName())
              .brand(request.getBrand())
              .category(request.getCategory())
              .description(request.getDescription())
              .imageUrl(request.getImageUrl())
              .build();

      Product saved = productRepository.save(product);
      log.info("Produit créé : id={}, name={}", saved.getId(), saved.getName());
      return ProductMapper.toResponse(saved);
    } catch (Exception ex) {
      throw new CustomException(ex, ResponseMessageConstants.PRODUCT_CREATE_FAILURE);
    }
  }

  @Override
  @Transactional
  public ProductResponse quickCreate(UUID storeId, QuickCreateProductRequest request)
      throws CustomException {
    try {
      Store store =
          storeRepository
              .findById(storeId)
              .orElseThrow(
                  () -> new NotFoundException("Supérette introuvable avec l'id : " + storeId));

      // 1 — Créer le produit avec statut PENDING
      Product product =
          Product.builder()
              .name(request.getName())
              .brand(request.getBrand())
              .category(request.getCategory())
              .description(request.getDescription())
              .status(ProductStatus.PENDING)
              .createdByStore(store)
              .build();

      Product savedProduct = productRepository.save(product);

      // 2 — Code-barres : utiliser celui fourni, sinon en générer un INTERNAL
      if (request.getBarcode() != null && !request.getBarcode().isBlank()) {
        if (!barcodeRepository.existsByCode(request.getBarcode())) {
          Barcode barcode =
              Barcode.builder()
                  .code(request.getBarcode())
                  .type(BarcodeType.EAN)
                  .product(savedProduct)
                  .build();
          barcodeRepository.save(barcode);
        }
      } else {
        // Générer automatiquement un code interne (2000000000001, 2000000000002, ...)
        String nextCode =
            BarcodeGenerator.next(barcodeRepository.findMaxInternalCode().orElse(null));
        Barcode internalBarcode =
            Barcode.builder()
                .code(nextCode)
                .type(BarcodeType.INTERNAL)
                .product(savedProduct)
                .build();
        barcodeRepository.save(internalBarcode);
        log.info("Code-barres interne généré : {}", nextCode);
      }

      // 3 — Ajouter immédiatement au catalogue de la supérette
      StoreProduct storeProduct =
          StoreProduct.builder()
              .store(store)
              .product(savedProduct)
              .price(request.getPrice())
              .costPrice(request.getCostPrice())
              .stock(request.getStock())
              .stockMin(request.getStockMin())
              .build();
      storeProductRepository.save(storeProduct);

      log.info(
          "Produit créé rapidement (PENDING) : id={}, storeId={}", savedProduct.getId(), storeId);
      return ProductMapper.toResponse(
          productRepository.findById(savedProduct.getId()).orElseThrow());
    } catch (NotFoundException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CustomException(ex, ResponseMessageConstants.PRODUCT_QUICK_CREATE_FAILURE);
    }
  }

  @Override
  @Transactional
  public ProductResponse approve(UUID productId) throws CustomException {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new NotFoundException("Produit introuvable avec l'id : " + productId));

    product.setStatus(ProductStatus.APPROVED);
    Product saved = productRepository.save(product);
    log.info("Produit approuvé : id={}", saved.getId());
    return ProductMapper.toResponse(saved);
  }

  @Override
  public Page<ProductResponse> listPending(UUID storeId, Pageable pageable) {
    return productRepository
        .findByStatusAndCreatedByStoreId(ProductStatus.PENDING, storeId, pageable)
        .map(ProductMapper::toResponse);
  }

  @Override
  public ProductResponse getById(UUID id) throws CustomException {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Produit introuvable avec l'id : " + id));
    return ProductMapper.toResponse(product);
  }

  @Override
  public Page<ProductResponse> list(Pageable pageable) {
    return productRepository.findByActiveTrue(pageable).map(ProductMapper::toResponse);
  }

  @Override
  public Page<ProductResponse> search(String keyword, Pageable pageable) {
    return productRepository.search(keyword, pageable).map(ProductMapper::toResponse);
  }

  @Override
  public Page<ProductResponse> listByCategory(String category, Pageable pageable) {
    return productRepository
        .findByCategoryAndActiveTrue(category, pageable)
        .map(ProductMapper::toResponse);
  }

  @Override
  @Transactional
  public ProductResponse update(UUID id, UpdateProductRequest request) throws CustomException {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Produit introuvable avec l'id : " + id));

    if (request.getName() != null) product.setName(request.getName());
    if (request.getBrand() != null) product.setBrand(request.getBrand());
    if (request.getCategory() != null) product.setCategory(request.getCategory());
    if (request.getDescription() != null) product.setDescription(request.getDescription());
    if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
    if (request.getActive() != null) product.setActive(request.getActive());

    Product saved = productRepository.save(product);
    log.info("Produit mis à jour : id={}", saved.getId());
    return ProductMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public void delete(UUID id) throws CustomException {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Produit introuvable avec l'id : " + id));
    product.setActive(false);
    productRepository.save(product);
    log.info("Produit désactivé : id={}", id);
  }

  @Override
  @Transactional
  public ProductResponse addBarcode(UUID productId, CreateBarcodeRequest request)
      throws CustomException {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new NotFoundException("Produit introuvable avec l'id : " + productId));

    if (barcodeRepository.existsByCode(request.getCode())) {
      throw new ConflictException("Ce code-barres existe déjà : " + request.getCode());
    }

    Barcode barcode =
        Barcode.builder()
            .code(request.getCode())
            .type(request.getType() != null ? request.getType() : BarcodeType.EAN)
            .product(product)
            .build();

    barcodeRepository.save(barcode);
    log.info("Code-barres ajouté : code={}, productId={}", request.getCode(), productId);

    return ProductMapper.toResponse(productRepository.findById(productId).orElseThrow());
  }

  @Override
  @Transactional
  public ProductResponse generateInternalBarcode(UUID productId) throws CustomException {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new NotFoundException("Produit introuvable avec l'id : " + productId));

    String nextCode = BarcodeGenerator.next(barcodeRepository.findMaxInternalCode().orElse(null));

    Barcode barcode =
        Barcode.builder().code(nextCode).type(BarcodeType.INTERNAL).product(product).build();
    barcodeRepository.save(barcode);

    log.info("Code-barres interne généré : code={}, productId={}", nextCode, productId);
    return ProductMapper.toResponse(productRepository.findById(productId).orElseThrow());
  }

  @Override
  public ProductResponse.BarcodeResponse lookupBarcode(String code) throws CustomException {
    Barcode barcode =
        barcodeRepository
            .findByCode(code)
            .orElseThrow(() -> new NotFoundException("Code-barres introuvable : " + code));
    return new ProductResponse.BarcodeResponse(
        barcode.getId(), barcode.getCode(), barcode.getType());
  }
}
