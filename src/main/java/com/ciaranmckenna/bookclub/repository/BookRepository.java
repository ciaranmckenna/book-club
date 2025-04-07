package com.ciaranmckenna.bookclub.repository;

import com.ciaranmckenna.bookclub.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entity
 * Provides database operations for Book entities
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find books by title containing the given text (case insensitive)
     * @param title Title text to search for
     * @param pageable Pagination information
     * @return Page of books matching the title
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find books by author containing the given text (case insensitive)
     * @param author Author text to search for
     * @param pageable Pagination information
     * @return Page of books matching the author
     */
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    /**
     * Find books by publication date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @param pageable Pagination information
     * @return Page of books in the publication date range
     */
    Page<Book> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find a book by ISBN
     * @param isbn ISBN to search for
     * @return Optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Search books by title or author containing the given text (case insensitive)
     * @param searchTerm Text to search for in title or author
     * @param pageable Pagination information
     * @return Page of books matching the search term
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> searchByTitleOrAuthor(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find books that are in a reading list with the given ID
     * @param readingListId ID of the reading list
     * @return List of books in the reading list
     */
    @Query("SELECT b FROM Book b JOIN b.readingLists rl WHERE rl.id = :readingListId")
    List<Book> findBooksByReadingListId(@Param("readingListId") Long readingListId);
    
    /**
     * Check if a book exists with the given ISBN
     * @param isbn ISBN to check
     * @return true if a book exists with the ISBN
     */
    boolean existsByIsbn(String isbn);
} 