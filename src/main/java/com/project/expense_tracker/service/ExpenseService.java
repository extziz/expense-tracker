package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.dto.ExpenseResponse;
import com.project.expense_tracker.dto.UpdateExpenseRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ExpenseService {

    List<ExpenseResponse> getAllExpenses();

    ExpenseResponse getExpenseById(Long id);

    ExpenseResponse createExpense(CreateExpenseRequest request);

    ExpenseResponse updateExpense(Long id, UpdateExpenseRequest request);

    void deleteExpense(Long id);

    List<ExpenseResponse> getExpensesByCategory(Long categoryId);

    List<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate);

    List<ExpenseResponse> searchExpenses(String keyword);

    Map<String, Object> getExpenseSummary();

    BigDecimal getTotalByCategory(Long categoryId);

    Map<String, BigDecimal> getMonthlyExpenses();

    List<ExpenseResponse> getTopExpenses(int limit);

    List<ExpenseResponse> getExpensesAboveAverage();

    List<ExpenseResponse> getCurrentMonthExpenses();

    List<Map<String, Object>> getDetailedStatsByDateRange(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getDailySpendingTrend();

    List<ExpenseResponse> searchAll(String keyword);

    List<Map<String, Object>> getCategoryBreakdown();

}