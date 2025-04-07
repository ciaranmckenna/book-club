package com.ciaranmckenna.bookclub.controller;

import com.ciaranmckenna.bookclub.common.ApiResponse;
import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for book endpoints
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;
    
    /**
     * Create a new book (admin only)
     * @param bookDto Book data
     * @return ResponseEntity with the newly created book
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookDto bookDto) {
        try {
            BookDto createdBook = bookService.createBook(bookDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Book created successfully", createdBook));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all books with pagination and sorting
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
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
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
     * @param id Book ID
     * @return ResponseEntity with book
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long id) {
        try {
            BookDto book = bookService.getBookById(id);
            return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", book));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update a book (admin only)
     * @param id Book ID
     * @param bookDto Updated book data
     * @return ResponseEntity with updated book
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        try {
            BookDto updatedBook = bookService.updateBook(id, bookDto);
            return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updatedBook));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a book (admin only)
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Search books by title or author
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
            Page<BookDto> books = bookService.findBooksByPublicationDateRange(startDate, endDate, pageable);
            return ResponseEntity.ok(ApiResponse.success(
                    "Books published between " + startDate + " and " + endDate, books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Find books in a reading list
     * @param readingListId Reading list ID
     * @return ResponseEntity with list of books
     */
    @GetMapping("/in-reading-list/{readingListId}")
    public ResponseEntity<ApiResponse<List<BookDto>>> findBooksByReadingListId(@PathVariable Long readingListId) {
        try {
            List<BookDto> books = bookService.findBooksByReadingListId(readingListId);
            return ResponseEntity.ok(ApiResponse.success("Books in reading list", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 