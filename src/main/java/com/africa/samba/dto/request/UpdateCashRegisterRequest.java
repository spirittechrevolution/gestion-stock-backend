/**
 * Requête de modification de caisse.
 * <p>
 * Rôle requis : ADMIN. Seuls les administrateurs peuvent modifier une caisse.
 */
package com.africa.samba.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCashRegisterRequest {
    @Size(max = 50)
    private String label;

    @NotNull
    private Boolean active;
}
