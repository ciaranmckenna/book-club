package com.ciaranmckenna.bookclub.service.impl;

import com.ciaranmckenna.bookclub.dto.UserDto;
import com.ciaranmckenna.bookclub.dto.UserRegistrationDto;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import com.ciaranmckenna.bookclub.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of the UserService interface */
@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  /**
   * Register a new user
   *
   * @param registrationDto Registration data
   * @return Newly created user
   */
  @Override
  @Transactional
  public UserDto registerUser(UserRegistrationDto registrationDto) {
    // Check if username or email is already taken
    if (userRepository.existsByUsername(registrationDto.username())) {
      throw new IllegalArgumentException("Username is already taken");
    }
    if (userRepository.existsByEmail(registrationDto.email())) {
      throw new IllegalArgumentException("Email is already registered");
    }

    // Create new user
    User user = new User();
    user.setUsername(registrationDto.username());
    user.setEmail(registrationDto.email());
    user.setPassword(passwordEncoder.encode(registrationDto.password()));
    user.setFirstName(registrationDto.firstName());
    user.setLastName(registrationDto.lastName());
    user.setEnabled(true);

    // Set default role
    user.setRoles(new HashSet<>());
    user.addRole("ROLE_USER");

    // Save user
    User savedUser = userRepository.save(user);

    // Convert to DTO and return
    return convertToDto(savedUser);
  }

  /**
   * Get a user by ID
   *
   * @param id User ID
   * @return User with the specified ID
   * @throws EntityNotFoundException if user not found
   */
  @Override
  public UserDto getUserById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    return convertToDto(user);
  }

  /**
   * Get a user by username
   *
   * @param username Username
   * @return User with the specified username
   * @throws EntityNotFoundException if user not found
   */
  @Override
  public UserDto getUserByUsername(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username));
    return convertToDto(user);
  }

  /**
   * Update a user
   *
   * @param id User ID
   * @param userDto Updated user data
   * @return Updated user
   * @throws EntityNotFoundException if user not found
   */
  @Override
  @Transactional
  public UserDto updateUser(Long id, UserDto userDto) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

    // Check if email is changed and not already taken
    if (!user.getEmail().equals(userDto.email()) && userRepository.existsByEmail(userDto.email())) {
      throw new IllegalArgumentException("Email is already registered");
    }

    // Update user fields
    user.setEmail(userDto.email());
    user.setFirstName(userDto.firstName());
    user.setLastName(userDto.lastName());

    // Save updated user
    User updatedUser = userRepository.save(user);

    // Convert to DTO and return
    return convertToDto(updatedUser);
  }

  /**
   * Delete a user
   *
   * @param id User ID
   * @throws EntityNotFoundException if user not found
   */
  @Override
  @Transactional
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
  }

  /**
   * Get all users
   *
   * @return List of all users
   */
  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
  }

  /**
   * Check if a username is available
   *
   * @param username Username to check
   * @return true if the username is available
   */
  @Override
  public boolean isUsernameAvailable(String username) {
    return !userRepository.existsByUsername(username);
  }

  /**
   * Check if an email is available
   *
   * @param email Email to check
   * @return true if the email is available
   */
  @Override
  public boolean isEmailAvailable(String email) {
    return !userRepository.existsByEmail(email);
  }

  /**
   * Convert a User entity to a UserDto
   *
   * @param user User entity
   * @return UserDto
   */
  private UserDto convertToDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getCreatedAt(),
        user.getRoles());
  }
}
