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

@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString(exclude = {"books"}) // Exclude collections from toString
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty(message = "Category name cannot be empty")
  @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
  @Column(unique = true, nullable = false)
  private String name;

  @Size(max = 255, message = "Description cannot exceed 255 characters")
  @Column(length = 255)
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
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
    Category category = (Category) obj;
    return Objects.equals(id, category.id) &&
           Objects.equals(name, category.name) &&
           Objects.equals(description, category.description) &&
           Objects.equals(createdAt, category.createdAt) &&
           Objects.equals(updatedAt, category.updatedAt);
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
