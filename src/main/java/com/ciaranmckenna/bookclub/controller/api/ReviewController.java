package com.ciaranmckenna.bookclub.controller.api;

import com.ciaranmckenna.bookclub.dto.ReviewDto;
import com.ciaranmckenna.bookclub.security.CustomUserDetails;
import com.ciaranmckenna.bookclub.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

  @Autowired private ReviewService reviewService;

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ReviewDto> createReview(
      @Valid @RequestBody ReviewDto reviewDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ReviewDto createdReview = reviewService.createReview(reviewDto, userDetails.getUser().getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ReviewDto> updateReview(
      @PathVariable Long id,
      @Valid @RequestBody ReviewDto reviewDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ReviewDto updatedReview =
        reviewService.updateReview(id, reviewDto, userDetails.getUser().getId());
    return ResponseEntity.ok(updatedReview);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> deleteReview(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    reviewService.deleteReview(id, userDetails.getUser().getId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
    ReviewDto review = reviewService.getReviewById(id);
    return ResponseEntity.ok(review);
  }

  @GetMapping("/book/{bookId}")
  public ResponseEntity<List<ReviewDto>> getReviewsByBookId(@PathVariable Long bookId) {
    List<ReviewDto> reviews = reviewService.getReviewsByBookId(bookId);
    return ResponseEntity.ok(reviews);
  }

  @GetMapping("/book/{bookId}/paged")
  public ResponseEntity<Page<ReviewDto>> getReviewsByBookIdPaged(
      @PathVariable Long bookId, @PageableDefault(size = 10) Pageable pageable) {
    Page<ReviewDto> reviews = reviewService.getReviewsByBookId(bookId, pageable);
    return ResponseEntity.ok(reviews);
  }

  @GetMapping("/user")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ReviewDto>> getUserReviews(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<ReviewDto> reviews = reviewService.getReviewsByUserId(userDetails.getUser().getId());
    return ResponseEntity.ok(reviews);
  }

  @GetMapping("/book/{bookId}/stats")
  public ResponseEntity<ReviewStatsDto> getBookReviewStats(@PathVariable Long bookId) {
    Double averageRating = reviewService.getAverageRatingByBookId(bookId);
    Long reviewCount = reviewService.getReviewCountByBookId(bookId);

    ReviewStatsDto stats = new ReviewStatsDto();
    stats.setAverageRating(averageRating != null ? averageRating : 0.0);
    stats.setReviewCount(reviewCount);

    return ResponseEntity.ok(stats);
  }

  public static class ReviewStatsDto {
    private Double averageRating;
    private Long reviewCount;

    public Double getAverageRating() {
      return averageRating;
    }

    public void setAverageRating(Double averageRating) {
      this.averageRating = averageRating;
    }

    public Long getReviewCount() {
      return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
      this.reviewCount = reviewCount;
    }
  }
}
