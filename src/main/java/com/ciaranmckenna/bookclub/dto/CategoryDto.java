package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CategoryDto {

  private Long id;

  @NotEmpty(message = "Category name cannot be empty")
  @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
  private String name;

  @Size(max = 255, message = "Description cannot exceed 255 characters")
  private String description;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private int bookCount;
}
