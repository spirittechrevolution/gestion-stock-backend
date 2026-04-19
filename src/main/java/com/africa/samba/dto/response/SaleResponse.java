package com.africa.samba.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * DTO de réponse pour une vente.
 * Accessible aux rôles : EMPLOYEE, ADMIN (voir documentation Swagger des endpoints).
 */
@Data
@Builder
public class SaleResponse {
    private UUID id;
    private UUID cashRegisterSessionId;
    private UUID storeProductId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime soldAt;
}
