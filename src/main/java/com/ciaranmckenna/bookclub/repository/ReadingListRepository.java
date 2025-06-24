package com.ciaranmckenna.bookclub.repository;

import com.ciaranmckenna.bookclub.entity.ReadingList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository for ReadingList entity Provides database operations for ReadingList entities */
@Repository
public interface ReadingListRepository extends JpaRepository<ReadingList, Long> {

  /**
   * Find reading lists belonging to a user
   *
   * @param userId User ID
   * @return List of reading lists
   */
  List<ReadingList> findByUserId(Long userId);

  /**
   * Find reading lists belonging to a user with pagination
   *
   * @param userId User ID
   * @param pageable Pagination information
   * @return Page of reading lists
   */
  Page<ReadingList> findByUserId(Long userId, Pageable pageable);

  /**
   * Find reading lists by name containing the given text (case insensitive)
   *
   * @param name Name text to search for
   * @param pageable Pagination information
   * @return Page of reading lists matching the name
   */
  Page<ReadingList> findByNameContainingIgnoreCase(String name, Pageable pageable);

  /**
   * Find reading lists by name containing the given text (case insensitive) for a specific user
   *
   * @param name Name text to search for
   * @param userId User ID
   * @param pageable Pagination information
   * @return Page of reading lists matching the name for the user
   */
  Page<ReadingList> findByNameContainingIgnoreCaseAndUserId(
      String name, Long userId, Pageable pageable);

  /**
   * Find a reading list by ID with books eagerly loaded
   *
   * @param id Reading list ID
   * @return Optional containing the reading list if found
   */
  @EntityGraph(attributePaths = {"books"})
  Optional<ReadingList> findWithBooksById(Long id);

  /**
   * Find reading lists that contain a specific book
   *
   * @param bookId Book ID
   * @return List of reading lists containing the book
   */
  @Query("SELECT rl FROM ReadingList rl JOIN rl.books b WHERE b.id = :bookId")
  List<ReadingList> findReadingListsByBookId(@Param("bookId") Long bookId);

  /**
   * Count the number of reading lists belonging to a user
   *
   * @param userId User ID
   * @return Count of reading lists
   */
  long countByUserId(Long userId);
}
