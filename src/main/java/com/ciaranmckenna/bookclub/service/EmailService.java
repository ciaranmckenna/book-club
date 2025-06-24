package com.ciaranmckenna.bookclub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired private JavaMailSender mailSender;

  @Value("${spring.mail.username:noreply@bookclub.com}")
  private String fromEmail;

  public void sendPasswordResetEmail(String toEmail, String resetToken) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromEmail);
    message.setTo(toEmail);
    message.setSubject("Password Reset Request");
    message.setText(
        "To reset your password, click the following link: "
            + "http://localhost:8080/reset-password?token="
            + resetToken
            + "\n\nThis link will expire in 1 hour.");

    mailSender.send(message);
  }
}
