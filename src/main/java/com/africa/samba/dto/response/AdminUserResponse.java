package com.africa.samba.dto.response;

import com.africa.samba.codeLists.Role;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** Réponse retournée après la création réussie d'un administrateur Samba. */
@Getter
@Builder
public class AdminUserResponse {

  private UUID userId;
  private String keycloakId;
  private String email;
  private String phone;
  private String firstName;
  private String lastName;
  private Set<Role> roles;
}
