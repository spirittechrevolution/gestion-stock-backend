package com.africa.samba.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Convertisseur JWT → authorities Spring Security basé sur les rôles realm Keycloak.
 *
 * <p>Extrait les rôles depuis {@code realm_access.roles} du token JWT et les convertit en {@link
 * GrantedAuthority} avec le préfixe {@code SAMBA_}.
 */
@Slf4j
public class KeycloakJwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String ROLE_PREFIX = "SAMBA_";
  private static final String REALM_ACCESS_CLAIM = "realm_access";
  private static final String ROLES_KEY = "roles";

  @Override
  @SuppressWarnings("unchecked")
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
    if (realmAccess == null || !realmAccess.containsKey(ROLES_KEY)) {
      return Collections.emptyList();
    }

    List<String> roles = (List<String>) realmAccess.get(ROLES_KEY);
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
        .collect(Collectors.toList());
  }
}
