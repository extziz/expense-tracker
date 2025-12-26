package com.project.expense_tracker.controller;

import com.project.expense_tracker.exception.BudgetExceededException;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.ExpenseNotFoundException;
import com.project.expense_tracker.exception.InvalidExpenseException;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // GET all expenses
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // GET expense by ID
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        return ResponseEntity.ok(expense);
    }

    // POST - Create new expense
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        // 1. Validate Category
        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            throw new InvalidExpenseException("Category is required");
        }

        // Ensure category exists before doing budget math
        categoryRepository.findById(expense.getCategory().getId())
                .orElseThrow(() -> new CategoryNotFoundException(expense.getCategory().getId()));

        // 2. Calculate current month's total
        LocalDate now = LocalDate.now();

        // Fetch all
        List<Expense> thisMonthExpenses = expenseRepository.findAll().stream()
                .filter(e -> {
                    LocalDate date = e.getExpenseDate();
                    // Check Month and Year
                    return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
                })
                .toList();

        BigDecimal currentTotal = thisMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Check Budget
        BigDecimal limit = new BigDecimal("5000");
        BigDecimal newTotal = currentTotal.add(expense.getAmount());

        // Throw if newTotal > limit
        if (newTotal.compareTo(limit) >= 0) {
            throw new BudgetExceededException();
        }

        // 4. Save
        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    // PUT - Update expense
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody Expense expenseDetails) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        // Validate and set category
        if (expenseDetails.getCategory() != null && expenseDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(expenseDetails.getCategory().getId())
                    .orElseThrow(() -> new CategoryNotFoundException(expenseDetails.getCategory().getId()));
            expense.setCategory(category);
        }

        expense.setAmount(expenseDetails.getAmount());
        expense.setDescription(expenseDetails.getDescription());
        expense.setExpenseDate(expenseDetails.getExpenseDate());

        Expense updatedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(updatedExpense);
    }

    // DELETE expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        expenseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET expenses by category
    @GetMapping("/category/{categoryId}")
    public List<Expense> getExpensesByCategory(@PathVariable Long categoryId) {
        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        return expenseRepository.findByCategory_Id(categoryId);
    }

    // GET expenses by date range
    @GetMapping("/date-range")
    public List<Expense> getExpensesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new InvalidExpenseException("Start date must be before end date");
        }

        return expenseRepository.findByExpenseDateBetween(startDate, endDate);
    }

    // GET summary statistics
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        List<Expense> expenses = expenseRepository.findAll();

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpenses", expenses.size());
        summary.put("totalAmount", total);
        summary.put("averageAmount", expenses.isEmpty() ? BigDecimal.ZERO :
                total.divide(BigDecimal.valueOf(expenses.size()), 2, BigDecimal.ROUND_HALF_UP));

        return ResponseEntity.ok(summary);
    }
}