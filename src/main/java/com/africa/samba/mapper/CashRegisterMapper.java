package com.africa.samba.mapper;

import com.africa.samba.dto.response.CashRegisterResponse;
import com.africa.samba.entity.CashRegister;
import org.springframework.stereotype.Component;

@Component
public class CashRegisterMapper {
    public CashRegisterResponse toResponse(CashRegister entity) {
        if (entity == null) return null;
        return CashRegisterResponse.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .label(entity.getLabel())
                .active(entity.getActive())
                .build();
    }
}
