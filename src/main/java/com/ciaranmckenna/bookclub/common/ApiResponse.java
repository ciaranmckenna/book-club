package com.ciaranmckenna.bookclub.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common response format for all API endpoints
 *
 * @param <T> The type of data returned in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private String result; // SUCCESS or ERROR
  private String message; // success or error message
  private T data; // return object from service class, if successful

  /**
   * Create a success response
   *
   * @param message Success message
   * @param data Data to return
   * @param <T> Type of data
   * @return ApiResponse with SUCCESS result
   */
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>("SUCCESS", message, data);
  }

  /**
   * Create an error response
   *
   * @param message Error message
   * @param <T> Type parameter (no data returned for errors)
   * @return ApiResponse with ERROR result
   */
  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>("ERROR", message, null);
  }
}
