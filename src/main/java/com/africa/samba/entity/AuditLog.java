package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "audit_log", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditLog extends BaseEntity {
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "cash_register_id")
    private UUID cashRegisterId;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "sale_id")
    private UUID saleId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
