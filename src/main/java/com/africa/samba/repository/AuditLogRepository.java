package com.africa.samba.repository;

import com.africa.samba.entity.AuditLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByStoreId(UUID storeId);
    Page<AuditLog> findByStoreId(UUID storeId, Pageable pageable);

    List<AuditLog> findByUserId(UUID userId);
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);

    List<AuditLog> findByEventType(String eventType);
    Page<AuditLog> findByEventType(String eventType, Pageable pageable);

    List<AuditLog> findBySessionId(UUID sessionId);
    Page<AuditLog> findBySessionId(UUID sessionId, Pageable pageable);
}
