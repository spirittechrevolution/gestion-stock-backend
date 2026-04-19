package com.africa.samba.services.impl;

import com.africa.samba.services.interfaces.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/** Implémentation du service SMS via LAfricaMobile (LAMPUSH API). */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

  private final RestClient restClient;

  @Value("${lam.sms.url}")
  private String smsUrl;

  @Value("${lam.sms.account-id}")
  private String accountId;

  @Value("${lam.sms.password}")
  private String password;

  @Value("${lam.sms.sender}")
  private String sender;

  public SmsServiceImpl(@Qualifier("smsRestClient") RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void sendOtp(String phone, String code) {
    String message = "Votre code de vérification Samba est : " + code;
    try {
      String url =
          smsUrl
              + "?account_id="
              + accountId
              + "&password="
              + password
              + "&sender="
              + sender
              + "&mobile="
              + phone
              + "&message="
              + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
      restClient.get().uri(url).retrieve().body(String.class);
      log.info("SMS OTP envoyé à {}", phone);
    } catch (Exception e) {
      log.error("Erreur lors de l'envoi du SMS OTP à {} : {}", phone, e.getMessage());
    }
  }
}
