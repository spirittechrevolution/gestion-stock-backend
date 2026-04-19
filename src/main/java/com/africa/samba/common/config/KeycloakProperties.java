package com.africa.samba.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propriétés de configuration Keycloak liées au realm et au client OIDC.
 *
 * <p>Mappées depuis le préfixe {@code keycloak} dans application.yaml.
 */
@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {

  /** URL de base du serveur Keycloak (ex : {@code http://localhost:8180}). */
  private String authServerUrl;

  /** Nom du realm Keycloak (ex : {@code samba-platform}). */
  private String realm;

  /** Client ID OIDC (ex : {@code samba-client}). */
  private String clientId;

  /** Client secret OIDC. */
  private String clientSecret;
}
