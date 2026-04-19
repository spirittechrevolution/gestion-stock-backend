package com.africa.samba.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/** Mise à jour des préférences de l'utilisateur connecté. Tous les champs sont optionnels (PATCH sémantique). */
@Getter
@Setter
public class UserPreferencesRequest {

  private String langue;
  private String theme;
  private String taillePolice;

  private Boolean sonScanActif;
  private Boolean vibrationScanActif;
  private String modePaiementDefaut;
  private Boolean afficherRecuAuto;
  private String partageRecuDefaut;
  private Boolean confirmerViderPanier;
  private Boolean autoIncrementScan;

  private String vueCatalogue;
  private String triProduitsDefaut;
  private Boolean afficherProduitsRupture;

  private Boolean notifStockFaible;
  private Boolean notifRuptureStock;
  private Boolean notifPeremption;
  private Boolean notifBilanJournalier;
  private String heureBilanJournalier;

  @Min(5)
  @Max(60)
  private Integer timeoutSessionMinutes;

  private Boolean pinAchaqueOuverture;
  private String periodeRapportDefaut;
  private String devise;
}
