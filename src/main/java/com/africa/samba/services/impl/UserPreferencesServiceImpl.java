package com.africa.samba.services.impl;

import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.UserPreferencesRequest;
import com.africa.samba.dto.response.UserPreferencesResponse;
import com.africa.samba.entity.User;
import com.africa.samba.entity.UserPreferences;
import com.africa.samba.mapper.UserPreferencesMapper;
import com.africa.samba.repository.UserPreferencesRepository;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.UserPreferencesService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPreferencesServiceImpl implements UserPreferencesService {

  private final UserPreferencesRepository preferencesRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserPreferencesResponse getMyPreferences(UUID userId) {
    return preferencesRepository
        .findByUtilisateurId(userId)
        .map(UserPreferencesMapper::toResponse)
        .orElseGet(
            () -> {
              User user =
                  userRepository
                      .findById(userId)
                      .orElseThrow(
                          () -> new NotFoundException("Utilisateur introuvable : id=" + userId));
              UserPreferences prefs = UserPreferences.builder().utilisateur(user).build();
              UserPreferences saved = preferencesRepository.save(prefs);
              log.info("Préférences initialisées par défaut pour userId={}", userId);
              return UserPreferencesMapper.toResponse(saved);
            });
  }

  @Override
  @Transactional
  public UserPreferencesResponse updateMyPreferences(UUID userId, UserPreferencesRequest request) {
    UserPreferences prefs =
        preferencesRepository
            .findByUtilisateurId(userId)
            .orElseGet(
                () -> {
                  User user =
                      userRepository
                          .findById(userId)
                          .orElseThrow(
                              () ->
                                  new NotFoundException("Utilisateur introuvable : id=" + userId));
                  return preferencesRepository.save(
                      UserPreferences.builder().utilisateur(user).build());
                });

    if (request.getLangue() != null) prefs.setLangue(request.getLangue());
    if (request.getTheme() != null) prefs.setTheme(request.getTheme());
    if (request.getTaillePolice() != null) prefs.setTaillePolice(request.getTaillePolice());
    if (request.getSonScanActif() != null) prefs.setSonScanActif(request.getSonScanActif());
    if (request.getVibrationScanActif() != null)
      prefs.setVibrationScanActif(request.getVibrationScanActif());
    if (request.getModePaiementDefaut() != null)
      prefs.setModePaiementDefaut(request.getModePaiementDefaut());
    if (request.getAfficherRecuAuto() != null)
      prefs.setAfficherRecuAuto(request.getAfficherRecuAuto());
    if (request.getPartageRecuDefaut() != null)
      prefs.setPartageRecuDefaut(request.getPartageRecuDefaut());
    if (request.getConfirmerViderPanier() != null)
      prefs.setConfirmerViderPanier(request.getConfirmerViderPanier());
    if (request.getAutoIncrementScan() != null)
      prefs.setAutoIncrementScan(request.getAutoIncrementScan());
    if (request.getVueCatalogue() != null) prefs.setVueCatalogue(request.getVueCatalogue());
    if (request.getTriProduitsDefaut() != null)
      prefs.setTriProduitsDefaut(request.getTriProduitsDefaut());
    if (request.getAfficherProduitsRupture() != null)
      prefs.setAfficherProduitsRupture(request.getAfficherProduitsRupture());
    if (request.getNotifStockFaible() != null)
      prefs.setNotifStockFaible(request.getNotifStockFaible());
    if (request.getNotifRuptureStock() != null)
      prefs.setNotifRuptureStock(request.getNotifRuptureStock());
    if (request.getNotifPeremption() != null) prefs.setNotifPeremption(request.getNotifPeremption());
    if (request.getNotifBilanJournalier() != null)
      prefs.setNotifBilanJournalier(request.getNotifBilanJournalier());
    if (request.getHeureBilanJournalier() != null)
      prefs.setHeureBilanJournalier(request.getHeureBilanJournalier());
    if (request.getTimeoutSessionMinutes() != null)
      prefs.setTimeoutSessionMinutes(request.getTimeoutSessionMinutes());
    if (request.getPinAchaqueOuverture() != null)
      prefs.setPinAchaqueOuverture(request.getPinAchaqueOuverture());
    if (request.getPeriodeRapportDefaut() != null)
      prefs.setPeriodeRapportDefaut(request.getPeriodeRapportDefaut());
    if (request.getDevise() != null) prefs.setDevise(request.getDevise());

    UserPreferences saved = preferencesRepository.save(prefs);
    log.info("Préférences mises à jour pour userId={}", userId);
    return UserPreferencesMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public void updateFcmToken(UUID userId, String fcmToken) {
    if (!preferencesRepository.existsByUtilisateurId(userId)) {
      getMyPreferences(userId);
    }
    preferencesRepository.updateFcmToken(userId, fcmToken);
    log.info("Token FCM mis à jour pour userId={}", userId);
  }

  @Override
  @Transactional
  public UserPreferencesResponse resetToDefaults(UUID userId) {
    UserPreferences prefs =
        preferencesRepository
            .findByUtilisateurId(userId)
            .orElseGet(
                () -> {
                  User user =
                      userRepository
                          .findById(userId)
                          .orElseThrow(
                              () ->
                                  new NotFoundException("Utilisateur introuvable : id=" + userId));
                  return UserPreferences.builder().utilisateur(user).build();
                });

    prefs.resetToDefaults();
    UserPreferences saved = preferencesRepository.save(prefs);
    log.info("Préférences réinitialisées pour userId={}", userId);
    return UserPreferencesMapper.toResponse(saved);
  }
}
