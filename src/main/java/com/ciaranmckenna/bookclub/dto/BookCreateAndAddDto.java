package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a book and adding it to a reading list in a single operation
 */
public record BookCreateAndAddDto(
        @Valid @NotNull(message = "Book data cannot be null")
        BookDto bookData,
        
        @NotNull(message = "Reading list ID cannot be null")
        Long readingListId
) {
    /**
     * Custom canonical constructor with validation
     */
    public BookCreateAndAddDto {
        // Validation is handled by Jakarta Bean Validation annotations
    }
} 