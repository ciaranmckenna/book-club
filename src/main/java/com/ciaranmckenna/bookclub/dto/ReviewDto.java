package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReviewDto {

  private Long id;

  @NotNull(message = "Rating is required")
  @Min(value = 1, message = "Rating must be between 1 and 5")
  @Max(value = 5, message = "Rating must be between 1 and 5")
  private Integer rating;

  @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
  private String reviewText;

  @NotNull(message = "Book ID is required")
  private Long bookId;

  private Long userId;
  private String username;
  private String bookTitle;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
