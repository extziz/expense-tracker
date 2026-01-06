package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
import com.project.expense_tracker.model.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    /**
     * Get all categories
     * @return List of all categories
     */
    List<Category> getAllCategories();

    /**
     * Get category by ID
     * @param id Category ID
     * @return Category if found
     * @throws CategoryNotFoundException if not found
     */
    Category getCategoryById(Long id);

    /**
     * Create a new category
     * @param category Category to create
     * @return Created category with ID
     * @throws DuplicateCategoryException if name already exists
     */
    Category createCategory(Category category);

    /**
     * Update an existing category
     * @param id Category ID
     * @param category Updated category data
     * @return Updated category
     * @throws CategoryNotFoundException if not found
     * @throws DuplicateCategoryException if new name already exists
     */
    Category updateCategory(Long id, Category category);

    /**
     * Delete a category
     * @param id Category ID
     * @throws CategoryNotFoundException if not found
     */
    void deleteCategory(Long id);

    /**
     * Search categories by keyword
     * @param keyword Search keyword
     * @return List of matching categories
     */
    List<Category> searchCategories(String keyword);

    /**
     * Check if category exists by name
     * @param name Category name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Get category by name
     * @param name Category name
     * @return Category if found
     * @throws CategoryNotFoundException if not found
     */
    Category getCategoryByName(String name);

    /**
     * Get category stats by id
     * @param categoryId  Category id
     * @return Category statistics if found
     * @throws CategoryNotFoundException if not found
     */
    Map<String, Object> getCategoryStatistics(Long categoryId);
}