package com.ciaranmckenna.bookclub.repository;

import com.ciaranmckenna.bookclub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 * Provides database operations for User entities
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username
     * @param username Username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     * @param email Email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists with the given username
     * @param username Username to check
     * @return true if a user exists with the username
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists with the given email
     * @param email Email to check
     * @return true if a user exists with the email
     */
    boolean existsByEmail(String email);
} 