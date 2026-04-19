package com.africa.samba.common.util;

import com.africa.samba.common.exception.UnAuthorizedException;
import java.util.List;

/**
 * Utilitaire de vérification des rôles utilisateur.
 *
 * <p>Fournit des méthodes statiques pour vérifier qu'un utilisateur possède un rôle requis. Utilisé
 * dans les controllers pour protéger les endpoints sans annotation @PreAuthorize.
 */
public final class RoleGuard {

  private static final String ROLE_PREFIX = "SAMBA_";
  private static final String ADMIN = "ADMIN";
  private static final String OWNER = "OWNER";

  private RoleGuard() {}

  /**
   * Vérifie que l'utilisateur a le rôle ADMIN ou OWNER.
   *
   * @param parser extracteur de rôles JWT
   * @param request requête HTTP
   * @throws UnAuthorizedException si le rôle n'est pas présent
   */
  public static void requireAdmin(
      RequestHeaderParser parser, jakarta.servlet.http.HttpServletRequest request) {
    List<String> roles = parser.extractRoles(request);
    if (!roles.contains(ROLE_PREFIX + ADMIN)
        && !roles.contains(ADMIN)
        && !roles.contains(ROLE_PREFIX + OWNER)
        && !roles.contains(OWNER)) {
      throw new UnAuthorizedException("Accès refusé – rôle ADMIN requis");
    }
  }

  /**
   * Vérifie que l'utilisateur a le rôle super admin.
   *
   * @param parser extracteur de rôles JWT
   * @param request requête HTTP
   * @throws UnAuthorizedException si le rôle n'est pas présent
   */
  public static void requireSuperAdmin(
      RequestHeaderParser parser, jakarta.servlet.http.HttpServletRequest request) {
    List<String> roles = parser.extractRoles(request);
    if (!roles.contains(ROLE_PREFIX + ADMIN) && !roles.contains(ADMIN)) {
      throw new UnAuthorizedException("Accès refusé – rôle ADMIN requis");
    }
  }

  /**
   * Vérifie que l'utilisateur est authentifié (tout rôle).
   *
   * @param parser extracteur de rôles JWT
   * @param request requête HTTP
   * @throws UnAuthorizedException si aucun token valide
   */
  public static void requireAuthenticated(
      RequestHeaderParser parser, jakarta.servlet.http.HttpServletRequest request) {
    parser.extractKeycloakId(request);
  }
}
