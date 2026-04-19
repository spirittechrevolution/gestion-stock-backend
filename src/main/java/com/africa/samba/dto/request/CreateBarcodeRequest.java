package com.africa.samba.dto.request;

import com.africa.samba.codeLists.BarcodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBarcodeRequest {

  @NotBlank
  @Size(max = 50)
  private String code;

  private BarcodeType type;
}
