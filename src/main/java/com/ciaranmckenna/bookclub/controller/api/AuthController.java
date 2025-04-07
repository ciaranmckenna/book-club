package com.ciaranmckenna.bookclub.controller.api;

import com.ciaranmckenna.bookclub.common.ApiResponse;
import com.ciaranmckenna.bookclub.dto.LoginDto;
import com.ciaranmckenna.bookclub.dto.UserDto;
import com.ciaranmckenna.bookclub.dto.UserRegistrationDto;
import com.ciaranmckenna.bookclub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    private SecurityContextRepository securityContextRepository = 
            new HttpSessionSecurityContextRepository();
    
    /**
     * Register a new user
     * @param registrationDto Registration data
     * @return ResponseEntity with the newly created user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserDto userDto = userService.registerUser(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Authenticate a user
     * @param loginDto Login data
     * @param request HTTP request
     * @param response HTTP response
     * @return ResponseEntity with authentication result
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDto loginDto,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        try {
            // Create authentication token
            UsernamePasswordAuthenticationToken authRequest = 
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password());
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(authRequest);
            
            // Set security context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", "User authenticated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication failed: " + e.getMessage()));
        }
    }
} 