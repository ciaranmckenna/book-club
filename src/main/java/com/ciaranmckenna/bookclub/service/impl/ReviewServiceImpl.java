package com.ciaranmckenna.bookclub.service.impl;

import com.ciaranmckenna.bookclub.dto.ReviewDto;
import com.ciaranmckenna.bookclub.entity.Book;
import com.ciaranmckenna.bookclub.entity.Review;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.BookRepository;
import com.ciaranmckenna.bookclub.repository.ReviewRepository;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import com.ciaranmckenna.bookclub.service.ReviewService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

  @Autowired private ReviewRepository reviewRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private UserRepository userRepository;

  @Override
  public ReviewDto createReview(ReviewDto reviewDto, Long userId) {
    if (hasUserReviewedBook(reviewDto.getBookId(), userId)) {
      throw new RuntimeException("User has already reviewed this book");
    }

    Book book =
        bookRepository
            .findById(reviewDto.getBookId())
            .orElseThrow(() -> new RuntimeException("Book not found"));
    User user =
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    Review review = new Review();
    review.setRating(reviewDto.getRating());
    review.setReviewText(reviewDto.getReviewText());
    review.setBook(book);
    review.setUser(user);

    Review savedReview = reviewRepository.save(review);
    return convertToDto(savedReview);
  }

  @Override
  public ReviewDto updateReview(Long id, ReviewDto reviewDto, Long userId) {
    Review review =
        reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));

    if (!review.getUser().getId().equals(userId)) {
      throw new RuntimeException("User can only update their own reviews");
    }

    review.setRating(reviewDto.getRating());
    review.setReviewText(reviewDto.getReviewText());

    Review updatedReview = reviewRepository.save(review);
    return convertToDto(updatedReview);
  }

  @Override
  public void deleteReview(Long id, Long userId) {
    Review review =
        reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));

    if (!review.getUser().getId().equals(userId)) {
      throw new RuntimeException("User can only delete their own reviews");
    }

    reviewRepository.delete(review);
  }

  @Override
  public ReviewDto getReviewById(Long id) {
    Review review =
        reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    return convertToDto(review);
  }

  @Override
  public List<ReviewDto> getReviewsByBookId(Long bookId) {
    return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public Page<ReviewDto> getReviewsByBookId(Long bookId, Pageable pageable) {
    return reviewRepository.findByBookId(bookId, pageable).map(this::convertToDto);
  }

  @Override
  public List<ReviewDto> getReviewsByUserId(Long userId) {
    return reviewRepository.findByUserId(userId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public Double getAverageRatingByBookId(Long bookId) {
    return reviewRepository.getAverageRatingByBookId(bookId);
  }

  @Override
  public Long getReviewCountByBookId(Long bookId) {
    return reviewRepository.getReviewCountByBookId(bookId);
  }

  @Override
  public boolean hasUserReviewedBook(Long bookId, Long userId) {
    return reviewRepository.existsByBookIdAndUserId(bookId, userId);
  }

  private ReviewDto convertToDto(Review review) {
    ReviewDto dto = new ReviewDto();
    dto.setId(review.getId());
    dto.setRating(review.getRating());
    dto.setReviewText(review.getReviewText());
    dto.setBookId(review.getBook().getId());
    dto.setUserId(review.getUser().getId());
    dto.setUsername(review.getUser().getUsername());
    dto.setBookTitle(review.getBook().getTitle());
    dto.setCreatedAt(review.getCreatedAt());
    dto.setUpdatedAt(review.getUpdatedAt());
    return dto;
  }
}
