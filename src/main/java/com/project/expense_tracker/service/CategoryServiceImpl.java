package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.*;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
import com.project.expense_tracker.mapper.CategoryMapper;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toSummaryList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // Business rule: Check for duplicate names
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateCategoryException(request.getName());
        }

        // Convert DTO to Entity
        Category category = categoryMapper.toEntity(request);

        // Business rule: Set default color if not provided
        if (category.getColor() == null || category.getColor().isEmpty()) {
            category.setColor("#808080");
        }

        // Save and return DTO
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Business rule: Check if new name conflicts with another category
        if (request.getName() != null &&
                !existingCategory.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new DuplicateCategoryException(request.getName());
        }

        // Update only provided fields
        categoryMapper.updateEntityFromRequest(request, existingCategory);

        Category updated = categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(updated);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);
        return categoryMapper.toSummaryList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getCategoriesOrderedByName() {
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        return categoryMapper.toSummaryList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getUnusedCategories() {
        List<Category> categories = categoryRepository.findCategoriesWithoutExpenses();
        return categoryMapper.toResponseList(categories);
    }
}