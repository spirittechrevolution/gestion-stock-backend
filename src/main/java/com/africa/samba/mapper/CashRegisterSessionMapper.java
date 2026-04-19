package com.africa.samba.mapper;

import com.africa.samba.dto.response.CashRegisterSessionResponse;
import com.africa.samba.entity.CashRegisterSession;
import org.springframework.stereotype.Component;

@Component
public class CashRegisterSessionMapper {
    public CashRegisterSessionResponse toResponse(CashRegisterSession entity) {
        if (entity == null) return null;
        return CashRegisterSessionResponse.builder()
                .id(entity.getId())
                .cashRegisterId(entity.getCashRegister().getId())
                .userId(entity.getUser().getId())
                .openedById(entity.getOpenedBy().getId())
                .openedAt(entity.getOpenedAt())
                .closedAt(entity.getClosedAt())
                .build();
    }
}
