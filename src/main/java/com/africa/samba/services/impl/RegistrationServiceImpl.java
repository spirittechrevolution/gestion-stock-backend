package com.africa.samba.services.impl;

import com.africa.samba.codeLists.Role;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.util.OtpUtils;
import com.africa.samba.dto.request.RegisterCompleteRequest;
import com.africa.samba.dto.response.RegisterResponse;
import com.africa.samba.entity.OtpVerification;
import com.africa.samba.entity.User;
import com.africa.samba.entity.UserPreferences;
import com.africa.samba.mapper.UserMapper;
import com.africa.samba.repository.OtpVerificationRepository;
import com.africa.samba.repository.UserPreferencesRepository;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.EmailService;
import com.africa.samba.services.interfaces.KeycloakAdminService;
import com.africa.samba.services.interfaces.RegistrationService;
import com.africa.samba.services.interfaces.SmsService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service d'inscription mobile via OTP SMS.
 *
 * <p>Orchestration du flux en 3 étapes : envoi OTP → vérification OTP → inscription complète
 * (Keycloak + PostgreSQL + email de bienvenue).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

  private static final long OTP_TTL_MINUTES = 5;

  private final OtpVerificationRepository otpRepo;
  private final UserRepository userRepo;
  private final UserPreferencesRepository preferencesRepo;
  private final KeycloakAdminService keycloakAdminService;
  private final SmsService smsService;
  private final EmailService emailService;

  // ── Étape 1 : Envoi OTP ────────────────────────────────────────

  @Override
  @Transactional
  public void sendOtp(String phone) throws CustomException {
    if (userRepo.existsByPhone(phone)) {
      throw new CustomException(
          new IllegalArgumentException("Ce numéro de téléphone est déjà enregistré"),
          ResponseMessageConstants.USER_CREATE_FAILURE_ALREADY_EXISTS);
    }

    otpRepo
        .findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(phone, LocalDateTime.now())
        .ifPresent(
            old -> {
              old.setUsed(true);
              otpRepo.save(old);
            });

    String code = OtpUtils.generateOtp();
    OtpVerification otp =
        OtpVerification.builder()
            .phone(phone)
            .code(code)
            .expiresAt(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES))
            .build();
    otpRepo.save(otp);

    smsService.sendOtp(phone, code);
    log.info("OTP envoyé au numéro {}", phone);
  }

  // ── Étape 2 : Vérification OTP ─────────────────────────────────

  @Override
  @Transactional
  public void verifyOtp(String phone, String code) throws CustomException {
    OtpVerification otp =
        otpRepo
            .findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                phone, LocalDateTime.now())
            .orElseThrow(
                () ->
                    new CustomException(
                        new IllegalArgumentException("Aucun OTP valide trouvé pour " + phone),
                        ResponseMessageConstants.OTP_NOT_FOUND));

    if (!otp.getCode().equals(code)) {
      throw new CustomException(
          new IllegalArgumentException("Code OTP incorrect"), ResponseMessageConstants.OTP_INVALID);
    }

    otp.setUsed(true);
    otpRepo.save(otp);
    log.info("OTP vérifié avec succès pour le numéro {}", phone);
  }

  // ── Étape 3 : Inscription complète ─────────────────────────────

  @Override
  @Transactional
  public RegisterResponse register(RegisterCompleteRequest request) throws CustomException {
    if (request.getEmail() != null
        && !request.getEmail().isBlank()
        && userRepo.existsByEmail(request.getEmail())) {
      throw new CustomException(
          new IllegalArgumentException("Cet email est déjà utilisé"),
          ResponseMessageConstants.USER_CREATE_FAILURE_ALREADY_EXISTS);
    }
    if (userRepo.existsByPhone(request.getPhone())) {
      throw new CustomException(
          new IllegalArgumentException("Ce numéro de téléphone est déjà enregistré"),
          ResponseMessageConstants.USER_CREATE_FAILURE_ALREADY_EXISTS);
    }

    String keycloakId = keycloakAdminService.createUser(request);
    log.info("Utilisateur créé dans Keycloak: {}", keycloakId);

    try {
      keycloakAdminService.assignRole(keycloakId, Role.OWNER);

      User.UserBuilder<?, ?> userBuilder =
          User.builder()
              .keycloakId(keycloakId)
              .firstName(request.getFirstName())
              .lastName(request.getLastName())
              .phone(request.getPhone())
              .roles(new HashSet<>(Set.of(Role.OWNER)))
              .phoneVerified(true)
              .emailVerified(false);
      if (request.getEmail() != null && !request.getEmail().isBlank()) {
        userBuilder.email(request.getEmail());
      }
      User user = userBuilder.build();
      user = userRepo.save(user);
      log.info("Utilisateur persisté en DB: {}", user.getId());

      UserPreferences prefs = UserPreferences.builder().utilisateur(user).build();
      preferencesRepo.save(prefs);
      log.info("Préférences par défaut créées pour userId={}", user.getId());

      if (user.getEmail() != null && !user.getEmail().isBlank()) {
        emailService.sendWelcome(user.getEmail(), request.getFirstName());
      }

      return UserMapper.toRegisterResponse(user);

    } catch (Exception e) {
      log.error(
          "Échec inscription DB pour keycloakId={}. Rollback Keycloak en cours...", keycloakId);
      keycloakAdminService.deleteUser(keycloakId);
      throw e;
    }
  }
}
