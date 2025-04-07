package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.UserDto;
import com.ciaranmckenna.bookclub.dto.UserRegistrationDto;

import java.util.List;

/**
 * Service interface for managing users
 */
public interface UserService {
    
    /**
     * Register a new user
     * @param registrationDto Registration data
     * @return Newly created user
     */
    UserDto registerUser(UserRegistrationDto registrationDto);
    
    /**
     * Get a user by ID
     * @param id User ID
     * @return User with the specified ID
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    UserDto getUserById(Long id);
    
    /**
     * Get a user by username
     * @param username Username
     * @return User with the specified username
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    UserDto getUserByUsername(String username);
    
    /**
     * Update a user
     * @param id User ID
     * @param userDto Updated user data
     * @return Updated user
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    UserDto updateUser(Long id, UserDto userDto);
    
    /**
     * Delete a user
     * @param id User ID
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    void deleteUser(Long id);
    
    /**
     * Get all users
     * @return List of all users
     */
    List<UserDto> getAllUsers();
    
    /**
     * Check if a username is available
     * @param username Username to check
     * @return true if the username is available
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * Check if an email is available
     * @param email Email to check
     * @return true if the email is available
     */
    boolean isEmailAvailable(String email);
} 