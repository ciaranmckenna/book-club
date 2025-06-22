package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.ReviewDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

  ReviewDto createReview(ReviewDto reviewDto, Long userId);

  ReviewDto updateReview(Long id, ReviewDto reviewDto, Long userId);

  void deleteReview(Long id, Long userId);

  ReviewDto getReviewById(Long id);

  List<ReviewDto> getReviewsByBookId(Long bookId);

  Page<ReviewDto> getReviewsByBookId(Long bookId, Pageable pageable);

  List<ReviewDto> getReviewsByUserId(Long userId);

  Double getAverageRatingByBookId(Long bookId);

  Long getReviewCountByBookId(Long bookId);

  boolean hasUserReviewedBook(Long bookId, Long userId);
}
