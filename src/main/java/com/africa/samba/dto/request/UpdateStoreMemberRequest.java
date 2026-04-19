package com.africa.samba.dto.request;

import com.africa.samba.codeLists.StoreMemberRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStoreMemberRequest {

  private StoreMemberRole role;

  private Boolean active;
}
