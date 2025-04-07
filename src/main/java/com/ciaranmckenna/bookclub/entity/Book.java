package com.ciaranmckenna.bookclub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Book entity class
 * Represents a book in the system which can be added to reading lists
 */
@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Title cannot be empty")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Column(nullable = false)
    private String title;

    @NotEmpty(message = "Author cannot be empty")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    @Column(nullable = false)
    private String author;

    @NotNull(message = "Publication date cannot be null")
    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(length = 1000)
    private String description;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    private Set<ReadingList> readingLists = new HashSet<>();

    /**
     * PrePersist hook to set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * PreUpdate hook to update last modified timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 