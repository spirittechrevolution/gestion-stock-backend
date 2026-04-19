/**
 * Requête d'ouverture de session de caisse (assignation vendeur).
 * <p>
 * Rôle requis : ADMIN. Seuls les administrateurs peuvent ouvrir une session de caisse.
 */
package com.africa.samba.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class OpenCashRegisterSessionRequest {
    @NotNull
    private UUID userId; // vendeur à assigner
}
