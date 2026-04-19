package com.africa.samba.dto.response;

import com.africa.samba.codeLists.Role;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Réponse retournée après la création ou la consultation d'un administrateur Samba.
 * <p>
 * Accessible aux utilisateurs avec le rôle ADMIN ou SUPER_ADMIN.
 * La création nécessite le rôle SUPER_ADMIN, la consultation le rôle ADMIN ou SUPER_ADMIN.
 */
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
