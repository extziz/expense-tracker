package com.project.expense_tracker.controller;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // GET all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // GET category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return ResponseEntity.ok(category);
    }

    // POST - Create new category
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        // Check if category already exists
        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateCategoryException(category.getName());
        }

        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // PUT - Update category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category categoryDetails) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Check if new name already exists (and it's not the current category)
        if (!category.getName().equals(categoryDetails.getName()) &&
                categoryRepository.existsByName(categoryDetails.getName())) {
            throw new DuplicateCategoryException(categoryDetails.getName());
        }

        category.setName(categoryDetails.getName());
        category.setColor(categoryDetails.getColor());
        category.setDescription(categoryDetails.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(updatedCategory);
    }

    // DELETE category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET category by name
    @GetMapping("/search")
    public List<Category> search(@RequestParam String keyword) {
        return categoryRepository.findByNameContaining(keyword);
    }
}