package com.ciaranmckenna.bookclub.controller;

import com.ciaranmckenna.bookclub.common.ApiResponse;
import com.ciaranmckenna.bookclub.dto.BookCreateAndAddDto;
import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.dto.ReadingListDto;
import com.ciaranmckenna.bookclub.security.CustomUserDetails;
import com.ciaranmckenna.bookclub.service.BookService;
import com.ciaranmckenna.bookclub.service.ReadingListService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** REST controller for book endpoints */
@RestController
@RequestMapping("/api/books")
public class BookController {

  @Autowired private BookService bookService;

  @Autowired private ReadingListService readingListService;

  /**
   * Create a new book
   *
   * @param bookDto Book data
   * @param userDetails Currently authenticated user
   * @return ResponseEntity with the newly created book
   */
  @PostMapping("/create")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<BookDto>> createBook(
      @Valid @RequestBody BookCreateAndAddDto bookDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      // Validate ISBN format
      if (!bookDto
          .bookData()
          .isbn()
          .matches(
              "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3}[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4}[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X])[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>("ERROR", "Invalid ISBN format", null));
      }

      // Validate publication date is not in the future
      if (bookDto.bookData().publicationDate() != null
          && bookDto.bookData().publicationDate().isAfter(java.time.LocalDate.now())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>("ERROR", "Publication date cannot be in the future", null));
      }

      BookDto createdBook =
          bookService.createBook(bookDto.bookData(), userDetails.getUser().getId());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse<>("SUCCESS", "Book created successfully", createdBook));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", e.getMessage(), null));
    }
  }

  /**
   * Get all books with pagination and sorting
   *
   * @param page Page number
   * @param size Page size
   * @param sort Sort field
   * @param direction Sort direction
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public")
  public ResponseEntity<ApiResponse<Page<BookDto>>> getAllBooks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "title") String sort,
      @RequestParam(defaultValue = "asc") String direction) {
    try {
      Sort.Direction sortDirection =
          direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
      Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

      Page<BookDto> books = bookService.getAllBooks(pageable);
      return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Get a book by ID
   *
   * @param id Book ID
   * @return ResponseEntity with book
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long id) {
    try {
      BookDto book = bookService.getBookById(id);
      return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", book));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Update a book (admin only)
   *
   * @param id Book ID
   * @param bookDto Updated book data
   * @return ResponseEntity with updated book
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<BookDto>> updateBook(
      @PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
    try {
      BookDto updatedBook = bookService.updateBook(id, bookDto);
      return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updatedBook));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Delete a book (admin only)
   *
   * @param id Book ID
   * @return ResponseEntity with deletion result
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
    try {
      bookService.deleteBook(id);
      return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Search books by title or author
   *
   * @param searchTerm Search term
   * @param page Page number
   * @param size Page size
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public/search")
  public ResponseEntity<ApiResponse<Page<BookDto>>> searchBooks(
      @RequestParam String searchTerm,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<BookDto> books = bookService.searchBooks(searchTerm, pageable);
      return ResponseEntity.ok(ApiResponse.success("Search results", books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Find books by title
   *
   * @param title Title to search for
   * @param page Page number
   * @param size Page size
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public/by-title")
  public ResponseEntity<ApiResponse<Page<BookDto>>> findBooksByTitle(
      @RequestParam String title,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<BookDto> books = bookService.findBooksByTitle(title, pageable);
      return ResponseEntity.ok(ApiResponse.success("Books with title containing: " + title, books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Find books by author
   *
   * @param author Author to search for
   * @param page Page number
   * @param size Page size
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public/by-author")
  public ResponseEntity<ApiResponse<Page<BookDto>>> findBooksByAuthor(
      @RequestParam String author,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<BookDto> books = bookService.findBooksByAuthor(author, pageable);
      return ResponseEntity.ok(ApiResponse.success("Books by author containing: " + author, books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Find books by publication date range
   *
   * @param startDate Start date
   * @param endDate End date
   * @param page Page number
   * @param size Page size
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public/by-date-range")
  public ResponseEntity<ApiResponse<Page<BookDto>>> findBooksByPublicationDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<BookDto> books =
          bookService.findBooksByPublicationDateRange(startDate, endDate, pageable);
      return ResponseEntity.ok(
          ApiResponse.success("Books published between " + startDate + " and " + endDate, books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Find books in a reading list
   *
   * @param readingListId Reading list ID
   * @return ResponseEntity with list of books
   */
  @GetMapping("/in-reading-list/{readingListId}")
  public ResponseEntity<ApiResponse<List<BookDto>>> findBooksByReadingListId(
      @PathVariable Long readingListId) {
    try {
      List<BookDto> books = bookService.findBooksByReadingListId(readingListId);
      return ResponseEntity.ok(ApiResponse.success("Books in reading list", books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Create a new book and add it to a reading list in a single operation
   *
   * @param createAndAddDto Book data and reading list ID
   * @return ResponseEntity with the updated reading list
   */
  @PostMapping("/create-and-add")
  public ResponseEntity<ApiResponse<ReadingListDto>> createBookAndAdd(
      @Valid @RequestBody BookCreateAndAddDto createAndAddDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      // Validate ISBN format if provided
      if (createAndAddDto.bookData().isbn() != null
          && !createAndAddDto.bookData().isbn().isBlank()) {
        if (!createAndAddDto.bookData().isbn().matches("^[\\d-]+$")) {
          return ResponseEntity.badRequest()
              .body(
                  ApiResponse.error(
                      "Invalid ISBN format. ISBN must contain only digits and hyphens."));
        }

        // Check if book with this ISBN already exists
        Optional<BookDto> existingBook =
            bookService.findBookByIsbn(createAndAddDto.bookData().isbn());
        if (existingBook.isPresent()) {
          // Book exists, check if it's already in the reading list
          if (readingListService.isBookInReadingList(
              createAndAddDto.readingListId(), existingBook.get().id())) {
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Book is already in the reading list",
                    readingListService.getReadingListById(createAndAddDto.readingListId())));
          }

          // Book exists but not in reading list, add it
          ReadingListDto updatedReadingList =
              readingListService.addBookToReadingList(
                  createAndAddDto.readingListId(), existingBook.get().id());
          return ResponseEntity.ok(
              ApiResponse.success(
                  "Book already exists and was added to reading list successfully",
                  updatedReadingList));
        }
      }

      // Validate publication date
      if (createAndAddDto.bookData().publicationDate() != null
          && createAndAddDto.bookData().publicationDate().isAfter(java.time.LocalDate.now())) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Publication date cannot be in the future."));
      }

      // Create the book
      BookDto createdBook =
          bookService.createBook(createAndAddDto.bookData(), userDetails.getUser().getId());

      // Add the book to the reading list
      ReadingListDto updatedReadingList =
          readingListService.addBookToReadingList(
              createAndAddDto.readingListId(), createdBook.id());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              ApiResponse.success(
                  "Book created and added to reading list successfully", updatedReadingList));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
    }
  }

  /**
   * Partially update a book
   *
   * @param id Book ID
   * @param bookDto Updated book data (only non-null fields will be updated)
   * @return ResponseEntity with updated book
   */
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<BookDto>> partialUpdateBook(
      @PathVariable Long id, @RequestBody BookDto bookDto) {
    try {
      BookDto updatedBook = bookService.partialUpdateBook(id, bookDto);
      return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updatedBook));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Find books by category
   *
   * @param categoryId Category ID
   * @param page Page number
   * @param size Page size
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public/by-category/{categoryId}")
  public ResponseEntity<ApiResponse<Page<BookDto>>> findBooksByCategory(
      @PathVariable Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<BookDto> books = bookService.findBooksByCategory(categoryId, pageable);
      return ResponseEntity.ok(ApiResponse.success("Books in category", books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * Find books by category name
   *
   * @param categoryName Category name
   * @param page Page number
   * @param size Page size
   * @return ResponseEntity with page of books
   */
  @GetMapping("/public/by-category-name")
  public ResponseEntity<ApiResponse<Page<BookDto>>> findBooksByCategoryName(
      @RequestParam String categoryName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<BookDto> books = bookService.findBooksByCategoryName(categoryName, pageable);
      return ResponseEntity.ok(ApiResponse.success("Books in category: " + categoryName, books));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
