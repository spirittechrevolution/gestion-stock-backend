package com.africa.samba.codeLists;

/**
 * Statut d'un produit dans le catalogue global.
 *
 * <ul>
 *   <li>{@code PENDING} — produit créé à la volée par un employé, en attente de validation
 *   <li>{@code APPROVED} — produit validé par un OWNER/MANAGER/ADMIN, visible dans le catalogue
 * </ul>
 */
public enum ProductStatus {
  PENDING,
  APPROVED
}
