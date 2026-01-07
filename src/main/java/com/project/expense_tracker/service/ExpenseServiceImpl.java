package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.ExpenseNotFoundException;
import com.project.expense_tracker.exception.InvalidExpenseException;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    @Override
    public Expense createExpense(Expense expense) {

        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            throw new InvalidExpenseException("Category is required");
        }

        Category category = categoryRepository.findById(expense.getCategory().getId())
                .orElseThrow(() -> new CategoryNotFoundException(expense.getCategory().getId()));

        expense.setCategory(category);

        if (expense.getExpenseDate() == null) {
            expense.setExpenseDate(LocalDate.now());
        }

        return expenseRepository.save(expense);
    }

    @Override
    public Expense updateExpense(Long id, Expense expenseDetails) {
        Expense existingExpense = getExpenseById(id);

        // Update category if provided
        if (expenseDetails.getCategory() != null &&
                expenseDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(expenseDetails.getCategory().getId())
                    .orElseThrow(() -> new CategoryNotFoundException(
                            expenseDetails.getCategory().getId()));
            existingExpense.setCategory(category);
        }

        // Update other fields
        existingExpense.setAmount(expenseDetails.getAmount());
        existingExpense.setDescription(expenseDetails.getDescription());
        existingExpense.setExpenseDate(expenseDetails.getExpenseDate());

        return expenseRepository.save(existingExpense);
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        expenseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByCategory(Long categoryId) {
        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        return expenseRepository.findByCategory_Id(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        // Business rule: Validate date range
        if (startDate.isAfter(endDate)) {
            throw new InvalidExpenseException("Start date must be before or equal to end date");
        }

        return expenseRepository.findByExpenseDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> searchExpenses(String keyword) {
        return expenseRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExpenseSummary() {
        List<Expense> expenses = expenseRepository.findAll();

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = expenses.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpenses", expenses.size());
        summary.put("totalAmount", total);
        summary.put("averageAmount", average);

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalByCategory(Long categoryId) {
        List<Expense> expenses = getExpensesByCategory(categoryId);
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMonthlyExpenses() {
        List<Expense> expenses = expenseRepository.findAll();

        // Group by year-month and sum amounts
        Map<YearMonth, BigDecimal> monthlyTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> YearMonth.from(expense.getExpenseDate()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));

        // Convert to String keys and sort
        return monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @Override
    @Transactional
    public List<Expense> createMultipleExpenses(List<Expense> expenses) {

        if (expenses.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> categoryIds = expenses.stream()
                .map(e -> {
                    if (e.getCategory() == null || e.getCategory().getId() == null) {
                        throw new InvalidExpenseException("Category ID is required for all expenses");
                    }
                    return e.getCategory().getId();
                })
                .collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        for (Expense expense : expenses) {
            Long catId = expense.getCategory().getId();

            Category category = categoryMap.get(catId);
            if (category == null) {
                throw new CategoryNotFoundException(catId);
            }

            expense.setCategory(category);

            if (expense.getExpenseDate() == null) {
                expense.setExpenseDate(LocalDate.now());
            }
        }
        return expenseRepository.saveAll(expenses);
    }

    @Override
    public void deleteExpensesByCategory(Long categoryId){
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        List<Expense> expenses = expenseRepository.findByCategory_Id(categoryId);
        Set<Long> ids = expenses.stream().map(Expense::getId).collect(Collectors.toSet());
        expenseRepository.deleteAllById(ids);
    }
}