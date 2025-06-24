package com.ciaranmckenna.bookclub.service.impl;

import com.ciaranmckenna.bookclub.dto.CategoryDto;
import com.ciaranmckenna.bookclub.entity.Category;
import com.ciaranmckenna.bookclub.repository.CategoryRepository;
import com.ciaranmckenna.bookclub.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired private CategoryRepository categoryRepository;

  @Override
  public CategoryDto createCategory(CategoryDto categoryDto) {
    if (existsByName(categoryDto.getName())) {
      throw new RuntimeException(
          "Category with name '" + categoryDto.getName() + "' already exists");
    }

    Category category = new Category();
    category.setName(categoryDto.getName());
    category.setDescription(categoryDto.getDescription());

    Category savedCategory = categoryRepository.save(category);
    return convertToDto(savedCategory);
  }

  @Override
  public CategoryDto getCategoryById(Long id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    return convertToDto(category);
  }

  @Override
  public CategoryDto getCategoryByName(String name) {
    Category category =
        categoryRepository
            .findByName(name)
            .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
    return convertToDto(category);
  }

  @Override
  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public Page<CategoryDto> getAllCategories(Pageable pageable) {
    return categoryRepository.findAll(pageable).map(this::convertToDto);
  }

  @Override
  public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

    if (!category.getName().equals(categoryDto.getName()) && existsByName(categoryDto.getName())) {
      throw new RuntimeException(
          "Category with name '" + categoryDto.getName() + "' already exists");
    }

    category.setName(categoryDto.getName());
    category.setDescription(categoryDto.getDescription());

    Category updatedCategory = categoryRepository.save(category);
    return convertToDto(updatedCategory);
  }

  @Override
  public void deleteCategory(Long id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    categoryRepository.delete(category);
  }

  @Override
  public List<CategoryDto> searchCategoriesByName(String name) {
    return categoryRepository.findByNameContainingIgnoreCase(name).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<CategoryDto> getCategoriesOrderByName() {
    return categoryRepository.findAllOrderByName().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<CategoryDto> getCategoriesOrderByBookCount() {
    return categoryRepository.findAllOrderByBookCountDesc().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsByName(String name) {
    return categoryRepository.existsByName(name);
  }

  private CategoryDto convertToDto(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setDescription(category.getDescription());
    dto.setCreatedAt(category.getCreatedAt());
    dto.setUpdatedAt(category.getUpdatedAt());
    dto.setBookCount(category.getBooks() != null ? category.getBooks().size() : 0);
    return dto;
  }
}
