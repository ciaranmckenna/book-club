package com.ciaranmckenna.bookclub.service.impl;

import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.entity.Book;
import com.ciaranmckenna.bookclub.repository.BookRepository;
import com.ciaranmckenna.bookclub.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the BookService interface
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    
    /**
     * Create a new book
     * @param bookDto Book data
     * @return Newly created book
     */
    @Override
    @Transactional
    public BookDto createBook(BookDto bookDto) {
        // Check if ISBN is provided and not already taken
        if (bookDto.isbn() != null && !bookDto.isbn().isBlank() && 
                bookRepository.existsByIsbn(bookDto.isbn())) {
            throw new IllegalArgumentException("ISBN is already registered for another book");
        }
        
        // Convert DTO to entity
        Book book = convertToEntity(bookDto);
        
        // Save book
        Book savedBook = bookRepository.save(book);
        
        // Convert to DTO and return
        return convertToDto(savedBook);
    }
    
    /**
     * Get a book by ID
     * @param id Book ID
     * @return Book with the specified ID
     * @throws EntityNotFoundException if book not found
     */
    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return convertToDto(book);
    }
    
    /**
     * Update a book
     * @param id Book ID
     * @param bookDto Updated book data
     * @return Updated book
     * @throws EntityNotFoundException if book not found
     */
    @Override
    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        
        // Check if ISBN is changed and not already taken
        if (bookDto.isbn() != null && !bookDto.isbn().isBlank() && 
                !bookDto.isbn().equals(book.getIsbn()) &&
                bookRepository.existsByIsbn(bookDto.isbn())) {
            throw new IllegalArgumentException("ISBN is already registered for another book");
        }
        
        // Update book fields
        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setPublicationDate(bookDto.publicationDate());
        book.setDescription(bookDto.description());
        book.setIsbn(bookDto.isbn());
        book.setCoverImageUrl(bookDto.coverImageUrl());
        
        // Save updated book
        Book updatedBook = bookRepository.save(book);
        
        // Convert to DTO and return
        return convertToDto(updatedBook);
    }
    
    /**
     * Delete a book
     * @param id Book ID
     * @throws EntityNotFoundException if book not found
     */
    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
    
    /**
     * Get all books
     * @param pageable Pagination information
     * @return Page of books
     */
    @Override
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    /**
     * Search books by title or author
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of books matching the search term
     */
    @Override
    public Page<BookDto> searchBooks(String searchTerm, Pageable pageable) {
        return bookRepository.searchByTitleOrAuthor(searchTerm, pageable)
                .map(this::convertToDto);
    }
    
    /**
     * Find books by title
     * @param title Title to search for
     * @param pageable Pagination information
     * @return Page of books matching the title
     */
    @Override
    public Page<BookDto> findBooksByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::convertToDto);
    }
    
    /**
     * Find books by author
     * @param author Author to search for
     * @param pageable Pagination information
     * @return Page of books matching the author
     */
    @Override
    public Page<BookDto> findBooksByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable)
                .map(this::convertToDto);
    }
    
    /**
     * Find books by publication date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @param pageable Pagination information
     * @return Page of books in the publication date range
     */
    @Override
    public Page<BookDto> findBooksByPublicationDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return bookRepository.findByPublicationDateBetween(startDate, endDate, pageable)
                .map(this::convertToDto);
    }
    
    /**
     * Find books in a reading list
     * @param readingListId Reading list ID
     * @return List of books in the reading list
     */
    @Override
    public List<BookDto> findBooksByReadingListId(Long readingListId) {
        return bookRepository.findBooksByReadingListId(readingListId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a Book entity to a BookDto
     * @param book Book entity
     * @return BookDto
     */
    private BookDto convertToDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationDate(),
                book.getDescription(),
                book.getIsbn(),
                book.getCoverImageUrl()
        );
    }
    
    /**
     * Convert a BookDto to a Book entity
     * @param bookDto BookDto
     * @return Book entity
     */
    private Book convertToEntity(BookDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setPublicationDate(bookDto.publicationDate());
        book.setDescription(bookDto.description());
        book.setIsbn(bookDto.isbn());
        book.setCoverImageUrl(bookDto.coverImageUrl());
        return book;
    }
} 