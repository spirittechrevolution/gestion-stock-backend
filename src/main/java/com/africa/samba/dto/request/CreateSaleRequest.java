package com.africa.samba.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

/**
 * DTO pour enregistrer une vente.
 * Rôle requis : EMPLOYEE ou ADMIN (voir documentation Swagger des endpoints).
 */
@Data
public class CreateSaleRequest {
    @NotNull
    private UUID storeProductId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal unitPrice;
}
