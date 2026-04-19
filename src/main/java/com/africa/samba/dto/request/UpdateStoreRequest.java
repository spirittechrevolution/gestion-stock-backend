package com.africa.samba.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStoreRequest {

  @Size(min = 2, max = 100)
  private String name;

  @Size(max = 255)
  private String address;

  @Size(max = 20)
  private String phone;

  private Boolean active;
}
