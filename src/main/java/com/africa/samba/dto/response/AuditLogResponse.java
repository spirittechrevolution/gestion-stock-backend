/**
 * DTO de réponse pour un log d'audit.
 * <p>
 * Accessible aux ADMIN et OWNER pour consultation de l'historique d'une supérette.
 */
package com.africa.samba.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private String eventType;
    private UUID userId;
    private UUID storeId;
    private UUID cashRegisterId;
    private UUID sessionId;
    private UUID saleId;
    private String details;
    private LocalDateTime createdAt;
}
