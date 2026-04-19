/**
 * Requête de création de caisse.
 * <p>
 * Rôle requis : ADMIN. Seuls les administrateurs peuvent créer une caisse.
 */
package com.africa.samba.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddCashRegisterRequest {
    @NotNull
    @Positive
    private Integer number;

    @Size(max = 50)
    private String label;
}
