package com.africa.samba.repository;

import com.africa.samba.codeLists.OtpPurpose;
import com.africa.samba.entity.OtpVerification;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

  /** Retourne le dernier OTP non utilisé et non expiré pour un numéro et un usage donnés. */
  Optional<OtpVerification>
      findTopByPhoneAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
          String phone, OtpPurpose purpose, LocalDateTime now);

  /** Retourne le dernier OTP non utilisé et non expiré pour un numéro de téléphone donné. */
  Optional<OtpVerification> findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
      String phone, LocalDateTime now);

  /** Supprime tous les OTP expirés (nettoyage périodique). */
  @Modifying
  @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :now")
  void deleteExpired(@Param("now") LocalDateTime now);
}
