package com.ciaranmckenna.bookclub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Book entity class Represents a book in the system which can be added to reading lists */
@Entity
@Table(name = "books")
@Getter
@Setter
@ToString(exclude = {"readingLists", "createdBy", "categories"}) // Exclude collections and relationships from toString
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

  @Column(length = 1000)
  private String publisher;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "book_categories",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new HashSet<>();

  /**
   * Custom hashCode implementation that excludes lazy-loaded collections
   * to prevent ConcurrentModificationException during Hibernate operations
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, title, author, publicationDate, description, publisher, isbn, coverImageUrl, createdAt, updatedAt);
  }

  /**
   * Custom equals implementation that excludes lazy-loaded collections
   * to prevent ConcurrentModificationException during Hibernate operations
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Book book = (Book) obj;
    return Objects.equals(id, book.id) &&
           Objects.equals(title, book.title) &&
           Objects.equals(author, book.author) &&
           Objects.equals(publicationDate, book.publicationDate) &&
           Objects.equals(description, book.description) &&
           Objects.equals(publisher, book.publisher) &&
           Objects.equals(isbn, book.isbn) &&
           Objects.equals(coverImageUrl, book.coverImageUrl) &&
           Objects.equals(createdAt, book.createdAt) &&
           Objects.equals(updatedAt, book.updatedAt);
  }

  /** PrePersist hook to set creation timestamp and creator */
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  /** PreUpdate hook to update last modified timestamp */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
