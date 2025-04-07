package com.ciaranmckenna.bookclub.dto;

/**
 * Login Data Transfer Object
 * Used for user authentication
 */
public record LoginDto(
    String username,
    String password
) {
    /**
     * Canonical constructor with validation
     */
    public LoginDto {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
} 