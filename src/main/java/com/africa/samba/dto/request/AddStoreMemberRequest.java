package com.africa.samba.dto.request;

import com.africa.samba.codeLists.StoreMemberRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddStoreMemberRequest {

  @NotNull private UUID userId;

  @NotNull private StoreMemberRole role;
}
