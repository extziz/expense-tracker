package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.dto.ExpenseResponse;
import com.project.expense_tracker.dto.ExpenseSummaryResponse;
import com.project.expense_tracker.dto.UpdateExpenseRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ExpenseService {

    List<ExpenseSummaryResponse> getAllExpenses();

    ExpenseResponse getExpenseById(Long id);

    ExpenseResponse createExpense(CreateExpenseRequest request);

    ExpenseResponse updateExpense(Long id, UpdateExpenseRequest request);

    void deleteExpense(Long id);

    List<ExpenseSummaryResponse> getExpensesByCategory(Long categoryId);

    List<ExpenseSummaryResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate);

    List<ExpenseSummaryResponse> searchExpenses(String keyword);

    Map<String, Object> getExpenseSummary();

    BigDecimal getTotalByCategory(Long categoryId);

    Map<String, BigDecimal> getMonthlyExpenses();

    List<ExpenseSummaryResponse> getTopExpenses(int limit);

    List<ExpenseSummaryResponse> getExpensesAboveAverage();

    List<ExpenseSummaryResponse> getCurrentMonthExpenses();

    List<Map<String, Object>> getDetailedStatsByDateRange(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getDailySpendingTrend();

    List<ExpenseSummaryResponse> searchAll(String keyword);

    List<Map<String, Object>> getCategoryBreakdown();

}