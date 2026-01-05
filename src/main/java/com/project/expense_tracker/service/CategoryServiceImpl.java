package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
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

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Category createCategory(Category category) {
        // Business rule: Check for duplicate names
        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateCategoryException(category.getName());
        }

        // Business rule: Set default color if not provided
        if (category.getColor() == null || category.getColor().isEmpty()) {
            category.setColor("#808080"); // Default gray
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        Category existingCategory = getCategoryById(id);

        // Business rule: Check if new name conflicts with another category
        if (!existingCategory.getName().equals(categoryDetails.getName()) &&
                categoryRepository.existsByName(categoryDetails.getName())) {
            throw new DuplicateCategoryException(categoryDetails.getName());
        }

        // Update fields
        existingCategory.setName(categoryDetails.getName());
        existingCategory.setColor(categoryDetails.getColor());
        existingCategory.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(existingCategory);
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
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByNameContaining(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
    }
}