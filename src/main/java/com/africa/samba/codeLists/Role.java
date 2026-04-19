package com.africa.samba.codeLists;

/**
 * Rôles utilisateurs Samba — MVP supérettes.
 *
 * <ul>
 *   <li>{@code OWNER} — propriétaire d'une ou plusieurs supérettes (gestion complète)
 *   <li>{@code MANAGER} — gérant d'une supérette (opérations quotidiennes, stock, prix)
 *   <li>{@code EMPLOYEE} — employé (consultation catalogue, scan produits)
 *   <li>{@code ADMIN} — administrateur plateforme Samba
 * </ul>
 */
public enum Role {
  OWNER,
  MANAGER,
  EMPLOYEE,
  ADMIN;

  /** Préfixe Spring Security pour les authorities Keycloak. */
  public String getAuthority() {
    return "SAMBA_" + this.name();
  }

  /** True si le rôle appartient à l'équipe interne Samba. */
  public boolean isStaff() {
    return this == ADMIN;
  }
}
