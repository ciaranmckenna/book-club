package com.ciaranmckenna.bookclub.controller.api;

import com.ciaranmckenna.bookclub.dto.CategoryDto;
import com.ciaranmckenna.bookclub.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  @Autowired private CategoryService categoryService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
    CategoryDto createdCategory = categoryService.createCategory(categoryDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
  }

  @GetMapping
  public ResponseEntity<Page<CategoryDto>> getAllCategories(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<CategoryDto> categories = categoryService.getAllCategories(pageable);
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/all")
  public ResponseEntity<List<CategoryDto>> getAllCategoriesList() {
    List<CategoryDto> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
    CategoryDto category = categoryService.getCategoryById(id);
    return ResponseEntity.ok(category);
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
    CategoryDto category = categoryService.getCategoryByName(name);
    return ResponseEntity.ok(category);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryDto> updateCategory(
      @PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
    CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
    return ResponseEntity.ok(updatedCategory);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<CategoryDto>> searchCategories(@RequestParam String name) {
    List<CategoryDto> categories = categoryService.searchCategoriesByName(name);
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/ordered/name")
  public ResponseEntity<List<CategoryDto>> getCategoriesOrderByName() {
    List<CategoryDto> categories = categoryService.getCategoriesOrderByName();
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/ordered/book-count")
  public ResponseEntity<List<CategoryDto>> getCategoriesOrderByBookCount() {
    List<CategoryDto> categories = categoryService.getCategoriesOrderByBookCount();
    return ResponseEntity.ok(categories);
  }
}
