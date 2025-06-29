package com.ciaranmckenna.bookclub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * User entity class Represents a user in the system with authentication details and reading lists
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"readingLists", "password"}) // Exclude collections and sensitive data from toString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty(message = "Username cannot be empty")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Column(unique = true, nullable = false)
  private String username;

  @NotEmpty(message = "Email cannot be empty")
  @Email(message = "Email should be valid")
  @Column(unique = true, nullable = false)
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Column(nullable = false)
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ReadingList> readingLists = new HashSet<>();

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(name = "reset_token")
  private String resetToken;

  @Column(name = "reset_token_expiry")
  private LocalDateTime resetTokenExpiry;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  private Set<String> roles = new HashSet<>();

  /**
   * Custom hashCode implementation that excludes lazy-loaded collections
   * to prevent ConcurrentModificationException during Hibernate operations
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, username, email, firstName, lastName, createdAt, updatedAt, enabled, resetToken, resetTokenExpiry, roles);
  }

  /**
   * Custom equals implementation that excludes lazy-loaded collections
   * to prevent ConcurrentModificationException during Hibernate operations
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User user = (User) obj;
    return enabled == user.enabled &&
           Objects.equals(id, user.id) &&
           Objects.equals(username, user.username) &&
           Objects.equals(email, user.email) &&
           Objects.equals(firstName, user.firstName) &&
           Objects.equals(lastName, user.lastName) &&
           Objects.equals(createdAt, user.createdAt) &&
           Objects.equals(updatedAt, user.updatedAt) &&
           Objects.equals(resetToken, user.resetToken) &&
           Objects.equals(resetTokenExpiry, user.resetTokenExpiry) &&
           Objects.equals(roles, user.roles);
  }

  /**
   * Add role to user
   *
   * @param role Role name
   */
  public void addRole(String role) {
    roles.add(role);
  }

  /**
   * Remove role from user
   *
   * @param role Role name
   */
  public void removeRole(String role) {
    roles.remove(role);
  }

  /**
   * Add a new reading list to the user
   *
   * @param readingList ReadingList to add
   */
  public void addReadingList(ReadingList readingList) {
    readingLists.add(readingList);
    readingList.setUser(this);
  }

  /**
   * Remove a reading list from the user
   *
   * @param readingList ReadingList to remove
   */
  public void removeReadingList(ReadingList readingList) {
    readingLists.remove(readingList);
    readingList.setUser(null);
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
