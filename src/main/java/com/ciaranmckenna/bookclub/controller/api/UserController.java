package com.ciaranmckenna.bookclub.controller.api;

import com.ciaranmckenna.bookclub.common.ApiResponse;
import com.ciaranmckenna.bookclub.dto.UserDto;
import com.ciaranmckenna.bookclub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * Get all users
     * @return List of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get a user by ID
     * @param id User ID
     * @return User with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update a user
     * @param id User ID
     * @param userDto Updated user data
     * @return Updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a user
     * @param id User ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Check if a username is available
     * @param username Username to check
     * @return true if the username is available
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> isUsernameAvailable(@RequestParam String username) {
        try {
            boolean isAvailable = userService.isUsernameAvailable(username);
            return ResponseEntity.ok(ApiResponse.success(
                    isAvailable ? "Username is available" : "Username is already taken",
                    isAvailable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Check if an email is available
     * @param email Email to check
     * @return true if the email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> isEmailAvailable(@RequestParam String email) {
        try {
            boolean isAvailable = userService.isEmailAvailable(email);
            return ResponseEntity.ok(ApiResponse.success(
                    isAvailable ? "Email is available" : "Email is already registered",
                    isAvailable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 