package com.ciaranmckenna.bookclub.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * ReadingList Data Transfer Object
 * Used to transfer reading list data between layers without exposing entity details
 */
public record ReadingListDto(
    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    Long userId,
    String username,
    Set<BookDto> books
) {
    /**
     * Canonical constructor with validation
     */
    public ReadingListDto {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
} 