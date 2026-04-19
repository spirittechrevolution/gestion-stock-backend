package com.africa.samba.mapper;

import com.africa.samba.dto.response.AuditLogResponse;
import com.africa.samba.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLog entity) {
        if (entity == null) return null;
        return AuditLogResponse.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .userId(entity.getUserId())
                .storeId(entity.getStoreId())
                .cashRegisterId(entity.getCashRegisterId())
                .sessionId(entity.getSessionId())
                .saleId(entity.getSaleId())
                .details(entity.getDetails())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
