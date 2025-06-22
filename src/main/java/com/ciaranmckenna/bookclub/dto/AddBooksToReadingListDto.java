package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for adding multiple books to a reading list in a single operation
 */
public record AddBooksToReadingListDto(
        @NotNull(message = "Reading list ID cannot be null")
        Long readingListId,
        
        @NotEmpty(message = "Book IDs cannot be empty")
        List<Long> bookIds
) {
    /**
     * Custom canonical constructor with validation
     */
    public AddBooksToReadingListDto {
        // Validation is handled by Jakarta Bean Validation annotations
    }
} 