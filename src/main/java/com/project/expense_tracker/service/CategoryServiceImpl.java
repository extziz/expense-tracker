package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(ExpenseRepository expenseRepository,
                              CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
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

        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateCategoryException(category.getName());
        }

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

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCategoryStatistics(Long categoryId){
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        List<Expense> expenses = expenseRepository.findByCategory_Id(categoryId);

        if (expenses.isEmpty()) {
            Map<String, Object> emptySummary = new HashMap<>();
            emptySummary.put("Total expenses in category", BigDecimal.ZERO);
            emptySummary.put("Average expense amount", BigDecimal.ZERO);
            emptySummary.put("Number of expenses", 0);
            emptySummary.put("Highest expense", BigDecimal.ZERO);
            emptySummary.put("Lowest expense", BigDecimal.ZERO);
            return emptySummary;
        }

        List<BigDecimal> expenseAmounts = expenses.stream()
                .map(Expense::getAmount)
                .toList();
        BigDecimal total = expenseAmounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int numberOfExpenses = expenseAmounts.size();
        BigDecimal average = total.divide(BigDecimal.valueOf(numberOfExpenses), 2, RoundingMode.HALF_UP);
        BigDecimal maxExpense = expenseAmounts.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal minExpense = expenseAmounts.stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        Map<String, Object> summary = new HashMap<>();
        summary.put("Total expenses in category", total);
        summary.put("Average expense amount", average);
        summary.put("Number of expenses", numberOfExpenses);
        summary.put("Highest expense", maxExpense);
        summary.put("Lowest expense", minExpense);
        return summary;
    }
}