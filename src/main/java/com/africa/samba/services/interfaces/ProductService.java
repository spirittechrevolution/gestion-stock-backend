package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.CreateBarcodeRequest;
import com.africa.samba.dto.request.CreateProductRequest;
import com.africa.samba.dto.request.QuickCreateProductRequest;
import com.africa.samba.dto.request.UpdateProductRequest;
import com.africa.samba.dto.response.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  ProductResponse create(CreateProductRequest request) throws CustomException;

  ProductResponse quickCreate(UUID storeId, QuickCreateProductRequest request)
      throws CustomException;

  ProductResponse approve(UUID productId) throws CustomException;

  ProductResponse getById(UUID id) throws CustomException;

  Page<ProductResponse> list(Pageable pageable);

  Page<ProductResponse> listPending(UUID storeId, Pageable pageable);

  Page<ProductResponse> search(String keyword, Pageable pageable);

  Page<ProductResponse> listByCategory(String category, Pageable pageable);

  ProductResponse update(UUID id, UpdateProductRequest request) throws CustomException;

  void delete(UUID id) throws CustomException;

  ProductResponse addBarcode(UUID productId, CreateBarcodeRequest request) throws CustomException;

  ProductResponse generateInternalBarcode(UUID productId) throws CustomException;

  ProductResponse.BarcodeResponse lookupBarcode(String code) throws CustomException;
}
