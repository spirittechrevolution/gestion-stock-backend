package com.africa.samba.mapper;

import com.africa.samba.dto.response.StoreResponse;
import com.africa.samba.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

  private StoreMapper() {}

  public static StoreResponse toResponse(Store store) {
    return new StoreResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getPhone(),
        store.getActive(),
        store.getOwner().getId(),
        store.getOwner().getFullName(),
        store.getStoreProducts() != null ? store.getStoreProducts().size() : 0,
        store.getCreatedAt(),
        store.getUpdatedAt());
  }
}
