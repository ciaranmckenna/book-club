package com.ciaranmckenna.bookclub.common;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST API endpoints Captures exceptions and returns appropriate API
 * responses Only applied to controllers in the api package
 */
@RestControllerAdvice(basePackages = "com.ciaranmckenna.bookclub.controller.api")
public class GlobalExceptionHandler {

  /**
   * Helper method to create an error response entity
   *
   * @param message Error message
   * @param status HTTP status
   * @return ResponseEntity with ApiResponse
   */
  public static ResponseEntity<ApiResponse<?>> errorResponseEntity(
      String message, HttpStatus status) {
    ApiResponse<?> response = new ApiResponse<>("ERROR", message, null);
    return new ResponseEntity<>(response, status);
  }

  /**
   * Handle IllegalArgumentException
   *
   * @param ex Exception instance
   * @return Bad request response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    return errorResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle EntityNotFoundException
   *
   * @param ex Exception instance
   * @return Not found response
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
    return errorResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  /**
   * Handle AccessDeniedException
   *
   * @param ex Exception instance
   * @return Forbidden response
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
    return errorResponseEntity("Access denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  /**
   * Handle validation errors from @Valid annotations
   *
   * @param ex Exception instance
   * @return Bad request response with field errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ApiResponse<Map<String, String>> response =
        new ApiResponse<>("ERROR", "Validation failed", errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle data integrity violations (e.g., unique constraints)
   *
   * @param ex Exception instance
   * @return Conflict response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    return errorResponseEntity(
        "Database constraint violation: " + ex.getMostSpecificCause().getMessage(),
        HttpStatus.CONFLICT);
  }

  /**
   * Fallback handler for all other exceptions
   *
   * @param ex Exception instance
   * @return Internal server error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleAllUncaughtException(Exception ex) {
    return errorResponseEntity(
        "An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
