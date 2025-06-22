package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

  @Autowired private UserRepository userRepository;

  @Autowired private EmailService emailService;

  @Autowired private PasswordEncoder passwordEncoder;

  public boolean initiatePasswordReset(String email) {
    Optional<User> userOptional = userRepository.findByEmail(email);

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      String resetToken = UUID.randomUUID().toString();

      user.setResetToken(resetToken);
      user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
      userRepository.save(user);

      emailService.sendPasswordResetEmail(email, resetToken);
      return true;
    }

    return false;
  }

  public boolean validateResetToken(String token) {
    Optional<User> userOptional = userRepository.findByResetToken(token);

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      return user.getResetTokenExpiry().isAfter(LocalDateTime.now());
    }

    return false;
  }

  public boolean resetPassword(String token, String newPassword) {
    Optional<User> userOptional = userRepository.findByResetToken(token);

    if (userOptional.isPresent()) {
      User user = userOptional.get();

      if (user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
      }
    }

    return false;
  }
}
