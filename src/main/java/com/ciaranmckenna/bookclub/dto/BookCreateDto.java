package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * DTO for creating or updating a book
 */
public record BookCreateDto(
        @NotBlank(message = "Title cannot be empty")
        String title,
        
        @NotBlank(message = "Author cannot be empty")
        String author,
        
        @NotBlank(message = "ISBN cannot be empty")
        @Pattern(regexp = "^[\\d-]+$", message = "ISBN must contain only digits and hyphens")
        String isbn,
        
        @NotNull(message = "Publication date cannot be empty")
        @PastOrPresent(message = "Publication date cannot be in the future")
        LocalDate publicationDate,
        
        String publisher,
        
        String language,
        
        @Positive(message = "Page count must be positive")
        Integer pageCount,
        
        String description,
        
        String coverImageUrl
) {
    /**
     * Custom canonical constructor with validation
     */
    public BookCreateDto {
        // Validation is handled by Jakarta Bean Validation annotations
    }
} 