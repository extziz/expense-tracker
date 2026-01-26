package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.*;

import java.util.List;

public interface CategoryService {

    List<CategorySummaryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);

    List<CategorySummaryResponse> searchCategories(String keyword);

    boolean existsByName(String name);

    CategoryResponse getCategoryByName(String name);

    List<CategorySummaryResponse> getCategoriesOrderedByName();

    List<CategoryResponse> getUnusedCategories();
}