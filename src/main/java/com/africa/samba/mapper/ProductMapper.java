package com.africa.samba.mapper;

import com.africa.samba.dto.response.ProductResponse;
import com.africa.samba.entity.Product;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  private ProductMapper() {}

  public static ProductResponse toResponse(Product product) {
    List<ProductResponse.BarcodeResponse> barcodes =
        product.getBarcodes() != null
            ? product.getBarcodes().stream()
                .map(b -> new ProductResponse.BarcodeResponse(b.getId(), b.getCode(), b.getType()))
                .toList()
            : List.of();

    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getBrand(),
        product.getCategory(),
        product.getDescription(),
        product.getImageUrl(),
        product.getActive(),
        product.getStatus(),
        product.getCreatedByStore() != null ? product.getCreatedByStore().getId() : null,
        barcodes,
        product.getCreatedAt(),
        product.getUpdatedAt());
  }
}
