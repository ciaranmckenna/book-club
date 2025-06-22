package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.ReviewDto;
import com.ciaranmckenna.bookclub.entity.Book;
import com.ciaranmckenna.bookclub.entity.Review;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.BookRepository;
import com.ciaranmckenna.bookclub.repository.ReviewRepository;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import com.ciaranmckenna.bookclub.service.impl.ReviewServiceImpl;
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
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private Book testBook;
    private Review testReview;

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

        testReview = new Review();
        testReview.setId(1L);
        testReview.setRating(5);
        testReview.setReviewText("Great book!");
        testReview.setUser(testUser);
        testReview.setBook(testBook);
    }

    @Test
    void createReview_ValidData_ReturnsCreatedReview() {
        // Given
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setRating(5);
        reviewDto.setReviewText("Excellent book!");
        reviewDto.setBookId(1L);

        when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewDto result = reviewService.createReview(reviewDto, 1L);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_UserAlreadyReviewed_ThrowsException() {
        // Given
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setBookId(1L);

        when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> reviewService.createReview(reviewDto, 1L));
    }

    @Test
    void hasUserReviewedBook_UserHasReviewed_ReturnsTrue() {
        // Given
        when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

        // When
        boolean result = reviewService.hasUserReviewedBook(1L, 1L);

        // Then
        assertTrue(result);
        verify(reviewRepository).existsByBookIdAndUserId(1L, 1L);
    }
}