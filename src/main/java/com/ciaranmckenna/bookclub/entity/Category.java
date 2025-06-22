package com.ciaranmckenna.bookclub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
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
