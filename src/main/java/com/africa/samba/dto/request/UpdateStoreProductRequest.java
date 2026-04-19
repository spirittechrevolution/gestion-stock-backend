package com.africa.samba.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStoreProductRequest {

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal price;

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal costPrice;

  @Min(0)
  private Integer stock;

  @Min(0)
  private Integer stockMin;

  private Boolean active;
}
