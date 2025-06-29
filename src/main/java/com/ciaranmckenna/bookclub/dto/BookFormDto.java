package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for the book form in the web interface */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFormDto {

  @NotBlank(message = "Title cannot be empty")
  private String title;

  @NotBlank(message = "Author cannot be empty")
  private String author;

  @Pattern(regexp = "^$|^[\\d-]+$", message = "ISBN must contain only digits and hyphens")
  private String isbn;

  @NotNull(message = "Publication date cannot be empty")
  @PastOrPresent(message = "Publication date cannot be in the future")
  private LocalDate publicationDate;

  private String publisher;

  private String description;

  private String coverImageUrl;

  /**
   * Convert this form DTO to a BookDto for the service layer
   *
   * @return BookDto
   */
  public BookDto toBookDto() {
    return new BookDto(
        null, // ID is null for new books
        this.title,
        this.author,
        this.publicationDate,
        this.description,
        this.publisher,
        this.isbn,
        this.coverImageUrl);
  }

  /**
   * Create a BookFormDto from a BookDto (for editing)
   *
   * @param bookDto Source BookDto
   * @return BookFormDto
   */
  public static BookFormDto fromBookDto(BookDto bookDto) {
    return new BookFormDto(
        bookDto.title(),
        bookDto.author(),
        bookDto.isbn(),
        bookDto.publicationDate(),
        bookDto.publisher(),
        bookDto.description(),
        bookDto.coverImageUrl());
  }
}
