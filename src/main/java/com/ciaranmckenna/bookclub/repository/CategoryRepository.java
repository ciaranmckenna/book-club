package com.ciaranmckenna.bookclub.repository;

import com.ciaranmckenna.bookclub.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByName(String name);

  boolean existsByName(String name);

  List<Category> findByNameContainingIgnoreCase(String name);

  @Query("SELECT c FROM Category c ORDER BY c.name ASC")
  List<Category> findAllOrderByName();

  @Query("SELECT c FROM Category c LEFT JOIN c.books b GROUP BY c ORDER BY COUNT(b) DESC")
  List<Category> findAllOrderByBookCountDesc();
}
