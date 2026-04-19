package com.africa.samba.dto.response;

import com.africa.samba.codeLists.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Représentation publique d'un utilisateur retournée par l'API.
 * <p>
 * Accessible à tout utilisateur authentifié (ADMIN, OWNER, EMPLOYEE, etc.).
 */
public record UserResponse(
    UUID id,
    String keycloakId,

    // ── Identité ─────────────────────────────────────────────────
    String firstName,
    String lastName,
    String email,
    String phone,
    LocalDate dateOfBirth,

    // ── Localisation ─────────────────────────────────────────────
    String address,
    String city,
    String country,
    String language,

    // ── Avatar ───────────────────────────────────────────────────
    String avatarUrl,

    // ── Rôles & supérettes ──────────────────────────────────────────
    Set<Role> roles,
    java.util.List<UUID> storeIds,

    // ── Vérifications ────────────────────────────────────────────
    boolean emailVerified,
    boolean phoneVerified,

    // ── État du compte ────────────────────────────────────────────
    boolean active,
    LocalDateTime lastLoginAt,

    // ── Audit ────────────────────────────────────────────────────
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
