package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.ExpenseNotFoundException;
import com.project.expense_tracker.exception.InvalidExpenseException;
import com.project.expense_tracker.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ExpenseService {

    /**
     * Get all expenses
     * @return List of all expenses
     */
    List<Expense> getAllExpenses();

    /**
     * Get expense by ID
     * @param id Expense ID
     * @return Expense if found
     * @throws ExpenseNotFoundException if not found
     */
    Expense getExpenseById(Long id);

    /**
     * Create a new expense
     * @param expense Expense to create
     * @return Created expense with ID
     * @throws CategoryNotFoundException if category not found
     * @throws InvalidExpenseException if validation fails
     */
    Expense createExpense(Expense expense);

    /**
     * Update an existing expense
     * @param id Expense ID
     * @param expense Updated expense data
     * @return Updated expense
     * @throws ExpenseNotFoundException if not found
     * @throws CategoryNotFoundException if category not found
     */
    Expense updateExpense(Long id, Expense expense);

    /**
     * Delete an expense
     * @param id Expense ID
     * @throws ExpenseNotFoundException if not found
     */
    void deleteExpense(Long id);

    /**
     * Get expenses by category
     * @param categoryId Category ID
     * @return List of expenses in that category
     * @throws CategoryNotFoundException if category not found
     */
    List<Expense> getExpensesByCategory(Long categoryId);

    /**
     * Get expenses by date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of expenses in date range
     * @throws InvalidExpenseException if date range is invalid
     */
    List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Search expenses by description
     * @param keyword Search keyword
     * @return List of matching expenses
     */
    List<Expense> searchExpenses(String keyword);

    /**
     * Get expense summary statistics
     * @return Map with total count, sum, and average
     */
    Map<String, Object> getExpenseSummary();

    /**
     * Calculate total expenses by category
     * @param categoryId Category ID
     * @return Total amount for that category
     */
    BigDecimal getTotalByCategory(Long categoryId);

    /**
     * Get monthly expenses for current year
     * @return Map with month -> total amount
     */
    Map<String, BigDecimal> getMonthlyExpenses();

    List<Expense> createMultipleExpenses(List<Expense> expenses);

    void deleteExpensesByCategory(Long categoryId);

    Map<String, BigDecimal> getSpendingByCategory();

    void createExpenseWithIntentionalError();
}