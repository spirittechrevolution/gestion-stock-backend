/**
 * DTO de réponse pour une session de caisse.
 * <p>
 * Création réservée aux ADMIN, consultation accessible à tout utilisateur authentifié (ADMIN, OWNER, EMPLOYEE, etc.).
 */
package com.africa.samba.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CashRegisterSessionResponse {
    private UUID id;
    private UUID cashRegisterId;
    private UUID userId;
    private UUID openedById;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
