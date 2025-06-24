package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.entity.Book;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.BookRepository;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import com.ciaranmckenna.bookclub.service.impl.BookServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPublicationDate(LocalDate.of(2023, 1, 1));
        testBook.setCreatedBy(testUser);
    }

    @Test
    void getBookById_ExistingBook_ReturnsBookDto() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        BookDto result = bookService.getBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.title());
        assertEquals("Test Author", result.author());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_NonExistingBook_ThrowsException() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(999L));
        verify(bookRepository).findById(999L);
    }

    @Test
    void createBook_ValidData_ReturnsCreatedBook() {
        // Given
        BookDto bookDto = new BookDto(
            null, // id
            "New Book", // title
            "New Author", // author
            LocalDate.of(2024, 1, 1), // publicationDate
            "Description", // description
            "123456789", // isbn
            null // coverImageUrl
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        BookDto result = bookService.createBook(bookDto, 1L);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }
}