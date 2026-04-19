package com.africa.samba.services.impl;

import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.ConflictException;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.AddStoreProductRequest;
import com.africa.samba.dto.request.UpdateStoreProductRequest;
import com.africa.samba.dto.response.StoreProductResponse;
import com.africa.samba.entity.Product;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.StoreProduct;
import com.africa.samba.mapper.StoreProductMapper;
import com.africa.samba.repository.ProductRepository;
import com.africa.samba.repository.StoreProductRepository;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.services.interfaces.StoreProductService;
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
public class StoreProductServiceImpl implements StoreProductService {

  private final StoreProductRepository storeProductRepository;
  private final StoreRepository storeRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional
  public StoreProductResponse add(UUID storeId, AddStoreProductRequest request)
      throws CustomException {
    try {
      Store store =
          storeRepository
              .findById(storeId)
              .orElseThrow(
                  () -> new NotFoundException("Supérette introuvable avec l'id : " + storeId));

      Product product =
          productRepository
              .findById(request.getProductId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Produit introuvable avec l'id : " + request.getProductId()));

      if (storeProductRepository.existsByStoreIdAndProductId(storeId, request.getProductId())) {
        throw new ConflictException("Ce produit est déjà dans le catalogue de cette supérette");
      }

      StoreProduct sp =
          StoreProduct.builder()
              .store(store)
              .product(product)
              .price(request.getPrice())
              .stock(request.getStock())
              .stockMin(request.getStockMin())
              .build();

      StoreProduct saved = storeProductRepository.save(sp);
      log.info(
          "Produit ajouté à la supérette : storeId={}, productId={}",
          storeId,
          request.getProductId());
      return StoreProductMapper.toResponse(saved);
    } catch (NotFoundException | ConflictException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CustomException(ex, ResponseMessageConstants.STORE_PRODUCT_ADD_FAILURE);
    }
  }

  @Override
  @Transactional
  public StoreProductResponse update(
      UUID storeId, UUID storeProductId, UpdateStoreProductRequest request) throws CustomException {
    StoreProduct sp =
        storeProductRepository
            .findById(storeProductId)
            .filter(s -> s.getStore().getId().equals(storeId))
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Produit de supérette introuvable : storeProductId=" + storeProductId));

    if (request.getPrice() != null) sp.setPrice(request.getPrice());
    if (request.getStock() != null) sp.setStock(request.getStock());
    if (request.getStockMin() != null) sp.setStockMin(request.getStockMin());
    if (request.getActive() != null) sp.setActive(request.getActive());

    StoreProduct saved = storeProductRepository.save(sp);
    log.info("Produit supérette mis à jour : id={}", saved.getId());
    return StoreProductMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public void remove(UUID storeId, UUID storeProductId) throws CustomException {
    StoreProduct sp =
        storeProductRepository
            .findById(storeProductId)
            .filter(s -> s.getStore().getId().equals(storeId))
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Produit de supérette introuvable : storeProductId=" + storeProductId));
    sp.setActive(false);
    storeProductRepository.save(sp);
    log.info("Produit supérette désactivé : id={}", storeProductId);
  }

  @Override
  public Page<StoreProductResponse> listByStore(UUID storeId, Pageable pageable) {
    return storeProductRepository
        .findByStoreIdAndActiveTrue(storeId, pageable)
        .map(StoreProductMapper::toResponse);
  }

  @Override
  public StoreProductResponse scan(UUID storeId, String barcode) throws CustomException {
    StoreProduct sp =
        storeProductRepository
            .findByStoreIdAndBarcode(storeId, barcode)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Aucun produit trouvé pour ce code-barres dans cette supérette"));
    return StoreProductMapper.toResponse(sp);
  }

  @Override
  public Page<StoreProductResponse> lowStock(UUID storeId, Pageable pageable) {
    return storeProductRepository
        .findLowStock(storeId, pageable)
        .map(StoreProductMapper::toResponse);
  }
}
