package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.dto.ExpenseResponse;
import com.project.expense_tracker.dto.UpdateExpenseRequest;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.ExpenseNotFoundException;
import com.project.expense_tracker.exception.InvalidExpenseException;
import com.project.expense_tracker.mapper.ExpenseMapper;
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
import java.util.stream.Collectors;
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              CategoryRepository categoryRepository,
                              ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenseMapper.toResponseList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        // Convert DTO to Entity
        Expense expense = expenseMapper.toEntity(request);
        expense.setCategory(category);

        // Save and return DTO
        Expense saved = expenseRepository.save(expense);
        return expenseMapper.toResponse(saved);
    }

    @Override
    public ExpenseResponse updateExpense(Long id, UpdateExpenseRequest request) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        // Update category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            existingExpense.setCategory(category);
        }

        // Update other fields (only non-null values)
        expenseMapper.updateEntityFromRequest(request, existingExpense);

        Expense updated = expenseRepository.save(existingExpense);
        return expenseMapper.toResponse(updated);
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
    public List<ExpenseResponse> getExpensesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        List<Expense> expenses = expenseRepository.findByCategory_Id(categoryId);
        return expenseMapper.toResponseList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidExpenseException("Start date must be before or equal to end date");
        }
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
        return expenseMapper.toResponseList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> searchExpenses(String keyword) {
        List<Expense> expenses = expenseRepository.findByDescriptionContainingIgnoreCase(keyword);
        return expenseMapper.toResponseList(expenses);
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
        List<ExpenseResponse> expenses = getExpensesByCategory(categoryId);
        return expenses.stream()
                .map(ExpenseResponse::getAmount)
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
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getTopExpenses(int limit) {
        List<Expense> expenses = expenseRepository.findTop10ByOrderByAmountDesc();
        return expenseMapper.toResponseList(
                expenses.stream().limit(limit).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesAboveAverage() {
        List<Expense> expenses = expenseRepository.findExpensesAboveAverage();
        return expenseMapper.toResponseList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getCurrentMonthExpenses() {
        List<Expense> expenses = expenseRepository.findCurrentMonthExpenses();
        return expenseMapper.toResponseList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDetailedStatsByDateRange(
            LocalDate startDate, LocalDate endDate) {

        List<Object[]> results = expenseRepository.getDetailedCategoryStats(startDate, endDate);

        return results.stream().map(row -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("category", row[0]);
            stat.put("count", row[1]);
            stat.put("total", row[2]);
            stat.put("average", row[3]);
            stat.put("minimum", row[4]);
            stat.put("maximum", row[5]);
            return stat;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailySpendingTrend() {
        List<Object[]> results = expenseRepository.getDailySpendingLast30Days();

        return results.stream().map(row -> {
            Map<String, Object> day = new HashMap<>();
            day.put("date", row[0]);
            day.put("total", row[1]);
            return day;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> searchAll(String keyword) {
        List<Expense> expenses = expenseRepository.searchByKeyword(keyword);
        return expenseMapper.toResponseList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCategoryBreakdown() {
        List<Object[]> results = expenseRepository.getCategoryBreakdown();

        return results.stream().map(row -> {
            Map<String, Object> breakdown = new HashMap<>();
            breakdown.put("category", row[0]);
            breakdown.put("count", row[1]);
            breakdown.put("total", row[2]);
            breakdown.put("average", row[3]);
            return breakdown;
        }).collect(Collectors.toList());
    }
}

