package com.project.expense_tracker.controller;

import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        Expense created = expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody Expense expense) {
        Expense updated = expenseService.updateExpense(id, expense);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(expenseService.getExpensesByCategory(categoryId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(startDate, endDate));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Expense>> searchExpenses(@RequestParam String keyword) {
        return ResponseEntity.ok(expenseService.searchExpenses(keyword));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(expenseService.getExpenseSummary());
    }

    @GetMapping("/category/{categoryId}/total")
    public ResponseEntity<BigDecimal> getTotalByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(expenseService.getTotalByCategory(categoryId));
    }

    @GetMapping("/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyExpenses() {
        return ResponseEntity.ok(expenseService.getMonthlyExpenses());
    }

    @GetMapping("/top")
    public ResponseEntity<List<Expense>> getTopExpenses(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(expenseService.getTopExpenses(limit));
    }

    @GetMapping("/above-average")
    public ResponseEntity<List<Expense>> getExpensesAboveAverage() {
        return ResponseEntity.ok(expenseService.getExpensesAboveAverage());
    }

    @GetMapping("/current-month")
    public ResponseEntity<List<Expense>> getCurrentMonthExpenses() {
        return ResponseEntity.ok(expenseService.getCurrentMonthExpenses());
    }

    @GetMapping("/detailed-stats")
    public ResponseEntity<List<Map<String, Object>>> getDetailedStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getDetailedStatsByDateRange(startDate, endDate));
    }

    @GetMapping("/daily-trend")
    public ResponseEntity<List<Map<String, Object>>> getDailyTrend() {
        return ResponseEntity.ok(expenseService.getDailySpendingTrend());
    }

    @GetMapping("/search-all")
    public ResponseEntity<List<Expense>> searchAll(@RequestParam String keyword) {
        return ResponseEntity.ok(expenseService.searchAll(keyword));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<List<Map<String, Object>>> getCategoryBreakdown() {
        return ResponseEntity.ok(expenseService.getCategoryBreakdown());
    }
}