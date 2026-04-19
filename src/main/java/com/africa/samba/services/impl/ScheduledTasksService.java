package com.africa.samba.services.impl;

import com.africa.samba.repository.OtpVerificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Tâches planifiées de maintenance périodique. */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasksService {

  private final OtpVerificationRepository otpVerificationRepository;

  /** Supprime les OTP expirés chaque heure pour éviter l'accumulation en base. */
  @Scheduled(fixedDelay = 3_600_000)
  @Transactional
  public void purgeExpiredOtps() {
    otpVerificationRepository.deleteExpired(LocalDateTime.now());
    log.debug("Nettoyage des OTP expirés effectué");
  }
}
