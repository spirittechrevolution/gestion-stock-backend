package com.africa.samba.mapper;

import com.africa.samba.dto.response.UserPreferencesResponse;
import com.africa.samba.entity.UserPreferences;
import org.springframework.stereotype.Component;

@Component
public class UserPreferencesMapper {

  private UserPreferencesMapper() {}

  public static UserPreferencesResponse toResponse(UserPreferences p) {
    return new UserPreferencesResponse(
        p.getId(),
        p.getUtilisateur().getId(),
        p.getLangue(),
        p.getTheme(),
        p.getTaillePolice(),
        p.getSonScanActif(),
        p.getVibrationScanActif(),
        p.getModePaiementDefaut(),
        p.getAfficherRecuAuto(),
        p.getPartageRecuDefaut(),
        p.getConfirmerViderPanier(),
        p.getAutoIncrementScan(),
        p.getVueCatalogue(),
        p.getTriProduitsDefaut(),
        p.getAfficherProduitsRupture(),
        p.getNotifStockFaible(),
        p.getNotifRuptureStock(),
        p.getNotifPeremption(),
        p.getNotifBilanJournalier(),
        p.getHeureBilanJournalier(),
        p.getTimeoutSessionMinutes(),
        p.getPinAchaqueOuverture(),
        p.getPeriodeRapportDefaut(),
        p.getDevise(),
        p.getUpdatedAt());
  }
}
