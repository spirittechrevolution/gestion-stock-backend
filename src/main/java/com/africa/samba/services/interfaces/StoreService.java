package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.CreateStoreRequest;
import com.africa.samba.dto.request.UpdateStoreRequest;
import com.africa.samba.dto.response.StoreResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

  StoreResponse create(UUID ownerId, CreateStoreRequest request) throws CustomException;

  StoreResponse getById(UUID id) throws CustomException;

  Page<StoreResponse> listByOwner(UUID ownerId, Pageable pageable);

  Page<StoreResponse> listAll(Pageable pageable);

  StoreResponse update(UUID id, UpdateStoreRequest request) throws CustomException;

  void delete(UUID id) throws CustomException;
}
