package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a new reading list
 * Used both for form binding and data transfer to service layer
 */
public record ReadingListCreateDto(
    @NotBlank(message = "Name cannot be empty")
    String name,
    
    String description
) {
    /**
     * Canonical constructor with validation only for non-form contexts
     * This allows empty objects to be created for form binding
     */
    public ReadingListCreateDto {
        // No constructor validation - we'll rely on Bean Validation annotations instead
    }
    
    /**
     * Helper method to validate this DTO outside of form context
     * @throws IllegalArgumentException if validation fails
     */
    public void validateForProcessing() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
    
    /**
     * Static factory method to create an empty DTO for form binding
     * @return Empty ReadingListCreateDto
     */
    public static ReadingListCreateDto createEmpty() {
        return new ReadingListCreateDto("", "");
    }
} 