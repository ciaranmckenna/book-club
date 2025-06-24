package com.ciaranmckenna.bookclub.repository;

import com.ciaranmckenna.bookclub.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findByBookId(Long bookId);

  Page<Review> findByBookId(Long bookId, Pageable pageable);

  List<Review> findByUserId(Long userId);

  Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);

  boolean existsByBookIdAndUserId(Long bookId, Long userId);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
  Double getAverageRatingByBookId(@Param("bookId") Long bookId);

  @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
  Long getReviewCountByBookId(@Param("bookId") Long bookId);

  @Query("SELECT r FROM Review r WHERE r.book.id = :bookId ORDER BY r.createdAt DESC")
  List<Review> findByBookIdOrderByCreatedAtDesc(@Param("bookId") Long bookId);
}
