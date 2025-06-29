package com.ciaranmckenna.bookclub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** ReadingList entity class Represents a reading list created by a user containing books */
@Entity
@Table(name = "reading_lists")
@Getter
@Setter
@ToString(exclude = {"books", "user"}) // Exclude collections and relationships from toString
public class ReadingList {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty(message = "Name cannot be empty")
  @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
  @Column(nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "reading_list_books",
      joinColumns = @JoinColumn(name = "reading_list_id"),
      inverseJoinColumns = @JoinColumn(name = "book_id"))
  private Set<Book> books = new HashSet<>();

  /**
   * Custom hashCode implementation that excludes lazy-loaded collections
   * to prevent ConcurrentModificationException during Hibernate operations
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, createdAt, updatedAt);
  }

  /**
   * Custom equals implementation that excludes lazy-loaded collections
   * to prevent ConcurrentModificationException during Hibernate operations
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    ReadingList that = (ReadingList) obj;
    return Objects.equals(id, that.id) &&
           Objects.equals(name, that.name) &&
           Objects.equals(description, that.description) &&
           Objects.equals(createdAt, that.createdAt) &&
           Objects.equals(updatedAt, that.updatedAt);
  }

  /**
   * Add a book to the reading list
   *
   * @param book Book to add
   */
  public void addBook(Book book) {
    books.add(book);
    book.getReadingLists().add(this);
  }

  /**
   * Remove a book from the reading list
   *
   * @param book Book to remove
   */
  public void removeBook(Book book) {
    books.remove(book);
    book.getReadingLists().remove(this);
  }

  /** PrePersist hook to set creation timestamp */
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
