package com.africa.samba.mapper;

import com.africa.samba.dto.response.RegisterResponse;
import com.africa.samba.dto.response.UserResponse;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper statique entre l'entité {@link User} et ses DTOs de sortie.
 *
 * <p>Centralise toutes les conversions {@code User → DTO} pour éviter la duplication dans les
 * services et controllers.
 *
 * <p>Usage :
 *
 * <pre>{@code
 * UserResponse response = UserMapper.toResponse(user);
 * RegisterResponse reg  = UserMapper.toRegisterResponse(user);
 * }</pre>
 */
@Component
public class UserMapper {

  private UserMapper() {}

  /**
   * Convertit un {@link User} en {@link UserResponse} complet.
   *
   * @param user entité à convertir (non null)
   * @return DTO de réponse
   */
  public static UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getKeycloakId(),
        // ── Identité ──────────────────────────────
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPhone(),
        user.getDateOfBirth(),
        // ── Localisation ──────────────────────────
        user.getAddress(),
        user.getCity(),
        user.getCountry(),
        user.getLanguage(),
        // ── Avatar ────────────────────────────────
        user.getAvatarUrl(),
        // ── Rôles & supérettes ────────────────────
        user.getRoles(),
        user.getStores() != null
            ? user.getStores().stream().map(Store::getId).toList()
            : java.util.List.of(),
        // ── Vérifications ─────────────────────────
        user.isEmailVerified(),
        user.isPhoneVerified(),
        // ── État du compte ────────────────────────
        user.isActive(),
        user.getLastLoginAt(),
        // ── Audit ─────────────────────────────────
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  /**
   * Convertit un {@link User} en {@link RegisterResponse} (vue post-inscription).
   *
   * @param user entité à convertir (non null)
   * @return DTO d'inscription
   */
  public static RegisterResponse toRegisterResponse(User user) {
    return RegisterResponse.builder()
        .userId(user.getId())
        .keycloakId(user.getKeycloakId())
        .email(user.getEmail())
        .phone(user.getPhone())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .roles(user.getRoles())
        .country(user.getCountry())
        .language(user.getLanguage())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
