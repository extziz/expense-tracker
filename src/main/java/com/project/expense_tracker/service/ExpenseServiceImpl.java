package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.ExpenseNotFoundException;
import com.project.expense_tracker.exception.InvalidExpenseException;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
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

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getSpendingByCategory(){
        List<Category> categoryList = categoryRepository.findAll();
        Map<String, BigDecimal> summary = categoryList.stream()
                .collect(Collectors.toMap(
                        Category::getName, c -> getTotalByCategory(c.getId())));
        return summary;
    }

    @Override
    @Transactional
    public void createExpenseWithIntentionalError() {

        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setExpenseDate(LocalDate.now());

        expenseRepository.save(expense);


        throw new RuntimeException("Simulated Error for Testing");
    }


    @Override
    @Transactional(readOnly = true)
    public List<Expense> getTopExpenses(int limit) {
        List<Expense> allExpenses = expenseRepository.findTop10ByOrderByAmountDesc();
        return allExpenses.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesAboveAverage() {
        return expenseRepository.findExpensesAboveAverage();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getCurrentMonthExpenses() {
        return expenseRepository.findCurrentMonthExpenses();
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
    public List<Expense> searchAll(String keyword) {
        return expenseRepository.searchByKeyword(keyword);
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

    @Override
    public List<Expense> getRecentExpenses(int days){
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return expenseRepository.findByExpenseDateAfter(cutoffDate);
    }

    @Override
    public List<Expense> getThisWeekExpenses(){
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return expenseRepository.findByExpenseDateBetween(startOfWeek, endOfWeek);
    }

    @Override
    public List<Expense> getLastWeekExpenses(){
        LocalDate today = LocalDate.now();
        LocalDate startOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return expenseRepository.findByExpenseDateBetween(startOfLastWeek, endOfLastWeek);
    }

    @Override
    public List<Expense> getThisYearExpenses(){
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        return expenseRepository.findByExpenseDateBetween(startOfYear, endOfYear);
    }

    @Override
    public List<Expense> getLastYearExpenses(){
        LocalDate today = LocalDate.now();
        LocalDate startOfLastYear = today.minusYears (1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfLastYear = today.minusYears(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return expenseRepository.findByExpenseDateBetween(startOfLastYear, endOfLastYear);
    }

    @Override
    public List<Expense> filterExpenses(Long categoryId, BigDecimal minAmount, BigDecimal maxAmount, LocalDate startDate, LocalDate endDate, String keyword) {

        Specification<Expense> spec = (root, query, cb) -> cb.conjunction();

        if (categoryId != null) {
            spec = spec.and(ExpenseSpecifications.hasCategory(categoryId));
        }

        if (minAmount != null) {
            spec = spec.and(ExpenseSpecifications.priceGreaterThan(minAmount));
        }

        if (maxAmount != null) {
            spec = spec.and(ExpenseSpecifications.priceLessThan(maxAmount));
        }

        if (startDate != null) {
            spec = spec.and(ExpenseSpecifications.dateAfterThe(startDate));
        }

        if (endDate != null) {
            spec = spec.and(ExpenseSpecifications.dateBeforeThe(endDate));
        }

        if (keyword != null) {
            spec = spec.and(ExpenseSpecifications.containsKeyword(keyword));
        }

        return expenseRepository.findAll(spec);
    }
}

