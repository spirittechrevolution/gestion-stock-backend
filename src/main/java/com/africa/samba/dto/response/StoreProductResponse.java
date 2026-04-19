package com.africa.samba.dto.response;

import com.africa.samba.codeLists.NiveauAlerte;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StoreProductResponse(
    UUID id,
    UUID storeId,
    UUID productId,
    String productName,
    String productBrand,
    String productCategory,
    BigDecimal price,
    BigDecimal costPrice,
    BigDecimal margin,
    int stock,
    int stockMin,
    NiveauAlerte niveauAlerte,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
