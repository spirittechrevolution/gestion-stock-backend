/**
 * DTO de réponse pour une caisse.
 * <p>
 * Accessible à tous les utilisateurs authentifiés (ADMIN, OWNER, EMPLOYEE, etc.).
 * Les opérations de création, modification, suppression nécessitent le rôle ADMIN.
 * Les opérations de consultation (get, list) sont accessibles à tout utilisateur connecté.
 */
package com.africa.samba.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CashRegisterResponse {
    private UUID id;
    private Integer number;
    private String label;
    private Boolean active;
}
