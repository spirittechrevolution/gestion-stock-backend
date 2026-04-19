package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.AddStoreProductRequest;
import com.africa.samba.dto.request.UpdateStoreProductRequest;
import com.africa.samba.dto.response.StoreProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreProductService {

  StoreProductResponse add(UUID storeId, AddStoreProductRequest request) throws CustomException;

  StoreProductResponse update(UUID storeId, UUID storeProductId, UpdateStoreProductRequest request)
      throws CustomException;

  void remove(UUID storeId, UUID storeProductId) throws CustomException;

  Page<StoreProductResponse> listByStore(UUID storeId, Pageable pageable);

  StoreProductResponse scan(UUID storeId, String barcode) throws CustomException;

  Page<StoreProductResponse> lowStock(UUID storeId, Pageable pageable);
}
