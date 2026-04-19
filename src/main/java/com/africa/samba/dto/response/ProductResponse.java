package com.africa.samba.dto.response;

import com.africa.samba.codeLists.BarcodeType;
import com.africa.samba.codeLists.ProductStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    String brand,
    String category,
    String description,
    String imageUrl,
    boolean active,
    ProductStatus status,
    UUID createdByStoreId,
    List<BarcodeResponse> barcodes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public record BarcodeResponse(UUID id, String code, BarcodeType type) {}
}
