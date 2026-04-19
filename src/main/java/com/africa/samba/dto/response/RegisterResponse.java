package com.africa.samba.dto.response;

import com.africa.samba.codeLists.Role;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** Réponse retournée après une inscription complète et réussie. */
@Getter
@Builder
public class RegisterResponse {

  private UUID userId;
  private String keycloakId;
  private String email;
  private String phone;
  private String firstName;
  private String lastName;
  private Set<Role> roles;
  private String country;
  private String language;
  private UUID boutiqueId;
  private String boutiqueNom;
  private LocalDateTime createdAt;
}
