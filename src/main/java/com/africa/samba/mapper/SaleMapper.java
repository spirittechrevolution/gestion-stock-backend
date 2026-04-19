package com.africa.samba.mapper;

import com.africa.samba.dto.response.SaleResponse;
import com.africa.samba.entity.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {
    public SaleResponse toResponse(Sale entity) {
        if (entity == null) return null;
        return SaleResponse.builder()
                .id(entity.getId())
                .cashRegisterSessionId(entity.getCashRegisterSession().getId())
                .storeProductId(entity.getStoreProduct().getId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .soldAt(entity.getSoldAt())
                .build();
    }
}
