package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.CategoryDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  CategoryDto createCategory(CategoryDto categoryDto);

  CategoryDto getCategoryById(Long id);

  CategoryDto getCategoryByName(String name);

  List<CategoryDto> getAllCategories();

  Page<CategoryDto> getAllCategories(Pageable pageable);

  CategoryDto updateCategory(Long id, CategoryDto categoryDto);

  void deleteCategory(Long id);

  List<CategoryDto> searchCategoriesByName(String name);

  List<CategoryDto> getCategoriesOrderByName();

  List<CategoryDto> getCategoriesOrderByBookCount();

  boolean existsByName(String name);
}
