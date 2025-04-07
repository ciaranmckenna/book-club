package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing books
 */
public interface BookService {
    
    /**
     * Create a new book
     * @param bookDto Book data
     * @return Newly created book
     */
    BookDto createBook(BookDto bookDto);
    
    /**
     * Get a book by ID
     * @param id Book ID
     * @return Book with the specified ID
     * @throws jakarta.persistence.EntityNotFoundException if book not found
     */
    BookDto getBookById(Long id);
    
    /**
     * Update a book
     * @param id Book ID
     * @param bookDto Updated book data
     * @return Updated book
     * @throws jakarta.persistence.EntityNotFoundException if book not found
     */
    BookDto updateBook(Long id, BookDto bookDto);
    
    /**
     * Delete a book
     * @param id Book ID
     * @throws jakarta.persistence.EntityNotFoundException if book not found
     */
    void deleteBook(Long id);
    
    /**
     * Get all books
     * @param pageable Pagination information
     * @return Page of books
     */
    Page<BookDto> getAllBooks(Pageable pageable);
    
    /**
     * Search books by title or author
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of books matching the search term
     */
    Page<BookDto> searchBooks(String searchTerm, Pageable pageable);
    
    /**
     * Find books by title
     * @param title Title to search for
     * @param pageable Pagination information
     * @return Page of books matching the title
     */
    Page<BookDto> findBooksByTitle(String title, Pageable pageable);
    
    /**
     * Find books by author
     * @param author Author to search for
     * @param pageable Pagination information
     * @return Page of books matching the author
     */
    Page<BookDto> findBooksByAuthor(String author, Pageable pageable);
    
    /**
     * Find books by publication date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @param pageable Pagination information
     * @return Page of books in the publication date range
     */
    Page<BookDto> findBooksByPublicationDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find books in a reading list
     * @param readingListId Reading list ID
     * @return List of books in the reading list
     */
    List<BookDto> findBooksByReadingListId(Long readingListId);
} 