package com.project.expense_tracker.controller;

import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategories(@RequestParam String keyword) {
        return ResponseEntity.ok(categoryService.searchCategories(keyword));
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<Category>> getCategoriesOrdered() {
        return ResponseEntity.ok(categoryService.getCategoriesOrderedByName());
    }

    @GetMapping("/with-expenses")
    public ResponseEntity<List<Category>> getCategoriesWithMinExpenses(
            @RequestParam(defaultValue = "1") int minCount) {
        return ResponseEntity.ok(categoryService.getCategoriesWithMinExpenses(minCount));
    }

    @GetMapping("/unused")
    public ResponseEntity<List<Category>> getUnusedCategories() {
        return ResponseEntity.ok(categoryService.getUnusedCategories());
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStatistics() {
        return ResponseEntity.ok(categoryService.getCategoryStatistics());
    }
}