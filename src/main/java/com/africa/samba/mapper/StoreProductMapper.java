package com.africa.samba.mapper;

import com.africa.samba.dto.response.StoreProductResponse;
import com.africa.samba.entity.Product;
import com.africa.samba.entity.StoreProduct;
import org.springframework.stereotype.Component;

@Component
public class StoreProductMapper {

  private StoreProductMapper() {}

  public static StoreProductResponse toResponse(StoreProduct sp) {
    Product product = sp.getProduct();
    return new StoreProductResponse(
        sp.getId(),
        sp.getStore().getId(),
        product.getId(),
        product.getName(),
        product.getBrand(),
        product.getCategory(),
        sp.getPrice(),
        sp.getStock(),
        sp.getStockMin() != null ? sp.getStockMin() : 0,
        sp.getNiveauAlerte(),
        sp.getActive(),
        sp.getCreatedAt(),
        sp.getUpdatedAt());
  }
}
