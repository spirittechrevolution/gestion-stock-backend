package com.africa.samba.services.impl;

import com.africa.samba.services.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/** Implémentation du service d'envoi d'emails via Spring Mail + Thymeleaf. */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${samba.mail.from}")
  private String fromAddress;

  @Value("${samba.mail.from-name:Samba POS}")
  private String fromName;

  @Override
  public void sendWelcome(String email, String firstName) {
    try {
      Context ctx = new Context();
      ctx.setVariable("firstName", firstName);
      String html = templateEngine.process("welcome", ctx);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromAddress, fromName);
      helper.setTo(email);
      helper.setSubject("Bienvenue sur Samba POS !");
      helper.setText(html, true);
      mailSender.send(message);
      log.info("Email de bienvenue envoyé à {}", email);
    } catch (MessagingException | java.io.UnsupportedEncodingException e) {
      log.error("Erreur lors de l'envoi de l'email de bienvenue à {} : {}", email, e.getMessage());
    }
  }
}
