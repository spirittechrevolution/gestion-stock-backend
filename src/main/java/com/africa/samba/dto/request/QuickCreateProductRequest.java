package com.africa.samba.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Requête de création rapide d'un produit par un employé.
 *
 * <p>Crée le produit en statut PENDING dans le catalogue global ET l'ajoute immédiatement au
 * catalogue de la supérette avec prix et stock.
 */
@Getter
@Setter
public class QuickCreateProductRequest {

  // ── Infos produit ──────────────────────────────────────────────

  @NotBlank
  @Size(min = 1, max = 150)
  private String name;

  @Size(max = 100)
  private String brand;

  @NotBlank
  @Size(max = 100)
  private String category;

  @Size(max = 500)
  private String description;

  /** Code-barres scanné (optionnel — un code interne sera généré si absent) */
  @Size(max = 50)
  private String barcode;

  // ── Infos catalogue supérette ─────────────────────────────────

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal price;

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal costPrice;

  @Min(0)
  private int stock = 0;

  @Min(0)
  private int stockMin = 0;
}
