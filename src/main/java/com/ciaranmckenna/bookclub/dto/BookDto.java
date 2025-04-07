package com.ciaranmckenna.bookclub.dto;

import java.time.LocalDate;

/**
 * Book Data Transfer Object
 * Used to transfer book data between layers without exposing entity details
 */
public record BookDto(
    Long id,
    String title,
    String author,
    LocalDate publicationDate,
    String description,
    String isbn,
    String coverImageUrl
) {
    /**
     * Canonical constructor with validation
     */
    public BookDto {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (publicationDate == null) {
            throw new IllegalArgumentException("Publication date cannot be null");
        }
    }
} 