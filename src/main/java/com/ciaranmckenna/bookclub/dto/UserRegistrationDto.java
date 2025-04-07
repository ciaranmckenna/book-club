package com.ciaranmckenna.bookclub.dto;

/**
 * User Registration Data Transfer Object
 * Used for registering new users
 */
public record UserRegistrationDto(
    String username,
    String email,
    String password,
    String firstName,
    String lastName
) {
    /**
     * Static factory method to create an empty DTO for form initialization
     * This bypasses validation for the initial form display
     */
    public static UserRegistrationDto createEmpty() {
        return new UserRegistrationDto("", "", "", "", "");
    }
    
    /**
     * Canonical constructor with validation
     * Validation occurs when DTO is created from form submission
     */
    public UserRegistrationDto {
        // Only validate if the DTO is being created from form submission
        // We consider it a submission if username and email have actual values
        if (username != null && !username.isBlank()) {
            // If real values are being provided, then validate
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            if (password != null && password.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters");
            }
        }
    }
} 