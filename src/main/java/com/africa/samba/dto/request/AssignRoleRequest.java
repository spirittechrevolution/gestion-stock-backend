package com.africa.samba.dto.request;

import com.africa.samba.codeLists.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Requête d'attribution d'un rôle realm Keycloak à un utilisateur. */
@Getter
@Setter
public class AssignRoleRequest {

  @NotNull(message = "Le rôle est obligatoire")
  private Role role;
}
