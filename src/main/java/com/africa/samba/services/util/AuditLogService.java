package com.africa.samba.services.util;

import com.africa.samba.entity.AuditLog;
import com.africa.samba.repository.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public void log(String eventType, UUID userId, UUID storeId, UUID cashRegisterId, UUID sessionId, UUID saleId, String details) {
        AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .userId(userId)
                .storeId(storeId)
                .cashRegisterId(cashRegisterId)
                .sessionId(sessionId)
                .saleId(saleId)
                .details(details)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}
