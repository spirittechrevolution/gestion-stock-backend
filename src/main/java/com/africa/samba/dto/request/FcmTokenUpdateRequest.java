package com.africa.samba.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Mise à jour du token FCM pour les notifications push mobiles. */
@Getter
@Setter
public class FcmTokenUpdateRequest {

  @NotBlank(message = "Le token FCM est obligatoire")
  private String fcmToken;
}
