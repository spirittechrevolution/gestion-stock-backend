package com.africa.samba.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddStoreProductRequest {

  @NotNull private UUID productId;

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
