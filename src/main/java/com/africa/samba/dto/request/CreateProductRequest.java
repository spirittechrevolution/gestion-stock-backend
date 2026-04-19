package com.africa.samba.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Requête de création de produit.
 * <p>
 * Rôle requis : ADMIN. Seuls les administrateurs peuvent créer un produit dans le catalogue global.
 */
public class CreateProductRequest {

  @NotBlank
  @Size(min = 1, max = 150)
  private String name;

  @Size(max = 100)
  private String brand;

  @NotBlank
  @Size(max = 100)
  private String category;

  @Size(max = 2000)
  private String description;

  @Size(max = 500)
  private String imageUrl;
}
