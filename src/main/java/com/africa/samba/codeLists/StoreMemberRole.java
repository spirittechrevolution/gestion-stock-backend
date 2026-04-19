package com.africa.samba.codeLists;

/**
 * Rôle d'un membre dans une supérette (table store_members).
 *
 * <p>Distinct de {@link Role} qui est le rôle global de l'utilisateur sur la plateforme. Ici on
 * parle du rôle <strong>au sein d'une supérette spécifique</strong>.
 */
public enum StoreMemberRole {
  MANAGER,
  EMPLOYEE
}
