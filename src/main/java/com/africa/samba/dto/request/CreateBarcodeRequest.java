package com.africa.samba.dto.request;

import com.africa.samba.codeLists.BarcodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Requête d'ajout de code-barres à un produit.
 * <p>
 * Rôle requis : ADMIN. Seuls les administrateurs peuvent ajouter un code-barres à un produit.
 */
public class CreateBarcodeRequest {

  @NotBlank
  @Size(max = 50)
  private String code;

  private BarcodeType type;
}
