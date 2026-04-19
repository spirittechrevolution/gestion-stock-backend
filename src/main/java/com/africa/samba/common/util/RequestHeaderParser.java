package com.africa.samba.common.util;

import com.africa.samba.common.constants.Constants;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

/**
 * Utilitaire pour extraire les informations du token JWT depuis les en-têtes HTTP.
 *
 * <p>Extrait le keycloakId (sub), les rôles et l'email à partir du header {@code Authorization:
 * Bearer ...}.
 */
@Component
@Slf4j
public class RequestHeaderParser {

  private final JwtDecoder jwtDecoder;

  public RequestHeaderParser(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  /**
   * Extrait le keycloakId (claim "sub") depuis le header Authorization.
   *
   * @param request requête HTTP entrante
   * @return keycloakId (UUID string)
   */
  public String extractKeycloakId(jakarta.servlet.http.HttpServletRequest request) {
    Jwt jwt = decodeToken(request);
    return jwt.getSubject();
  }

  /**
   * Extrait les rôles realm depuis le token JWT.
   *
   * @param request requête HTTP entrante
   * @return liste des noms de rôles
   */
  @SuppressWarnings("unchecked")
  public List<String> extractRoles(jakarta.servlet.http.HttpServletRequest request) {
    Jwt jwt = decodeToken(request);
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess != null && realmAccess.containsKey("roles")) {
      return (List<String>) realmAccess.get("roles");
    }
    return List.of();
  }

  /**
   * Extrait l'email depuis le token JWT.
   *
   * @param request requête HTTP entrante
   * @return email ou null
   */
  public String extractEmail(jakarta.servlet.http.HttpServletRequest request) {
    Jwt jwt = decodeToken(request);
    return jwt.getClaimAsString("email");
  }

  private Jwt decodeToken(jakarta.servlet.http.HttpServletRequest request) {
    String authHeader = request.getHeader(Constants.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new com.africa.samba.common.exception.UnAuthorizedException(
          "Token d'authentification manquant ou invalide");
    }
    String token = authHeader.substring(7);
    return jwtDecoder.decode(token);
  }
}
