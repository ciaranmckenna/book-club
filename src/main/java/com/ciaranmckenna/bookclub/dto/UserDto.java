package com.ciaranmckenna.bookclub.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User Data Transfer Object
 * Used to transfer user data between layers without exposing entity details
 */
public record UserDto(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    LocalDateTime createdAt,
    Set<String> roles
) {
    /**
     * Canonical constructor with validation
     */
    public UserDto {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
} 