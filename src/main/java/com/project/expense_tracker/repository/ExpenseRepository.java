package com.project.expense_tracker.repository;

import com.project.expense_tracker.model.Expense;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    // ========== Derived Query Methods ==========

    // By category
    List<Expense> findByCategory_Id(Long categoryId);

    List<Expense> findByCategory_Name(String categoryName);

    List<Expense> findByCategory_IdIn(List<Long> categoryIds);

    // By date
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByExpenseDateAfter(LocalDate date);

    List<Expense> findByExpenseDateBefore(LocalDate date);

    // By amount
    List<Expense> findByAmountGreaterThan(BigDecimal amount);

    List<Expense> findByAmountLessThan(BigDecimal amount);

    List<Expense> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // By description
    List<Expense> findByDescriptionContainingIgnoreCase(String keyword);

    List<Expense> findByDescriptionStartingWithIgnoreCase(String prefix);

    // Combined conditions
    List<Expense> findByCategory_IdAndExpenseDateBetween(
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense> findByCategory_IdAndAmountGreaterThan(
            Long categoryId,
            BigDecimal amount
    );

    // With ordering
    List<Expense> findTop10ByOrderByAmountDesc();

    List<Expense> findTop5ByCategory_IdOrderByExpenseDateDesc(Long categoryId);

    List<Expense> findByExpenseDateBetweenOrderByAmountDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    // ========== Custom JPQL Queries ==========

    // Get expenses with category info
    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id")
    Expense findByIdWithCategory(@Param("id") Long id);

    // Sum by category
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId")
    BigDecimal sumAmountByCategory(@Param("categoryId") Long categoryId);

    // Sum by date range
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Average by category
    @Query("SELECT AVG(e.amount) FROM Expense e WHERE e.category.id = :categoryId")
    BigDecimal averageAmountByCategory(@Param("categoryId") Long categoryId);

    // Count by category
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.category.id = :categoryId")
    Long countByCategory(@Param("categoryId") Long categoryId);

    // Get expenses above average
    @Query("SELECT e FROM Expense e WHERE e.amount > " +
            "(SELECT AVG(e2.amount) FROM Expense e2)")
    List<Expense> findExpensesAboveAverage();

    // Category breakdown
    @Query("SELECT e.category.name, COUNT(e), SUM(e.amount), AVG(e.amount) " +
            "FROM Expense e " +
            "GROUP BY e.category.name " +
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryBreakdown();

    // Search in multiple fields
    @Query("SELECT e FROM Expense e WHERE " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Expense> searchByKeyword(@Param("keyword") String keyword);

    // Get expenses for current month
    @Query("SELECT e FROM Expense e WHERE " +
            "FUNCTION('YEAR', e.expenseDate) = FUNCTION('YEAR', CURRENT_DATE) AND " +
            "FUNCTION('MONTH', e.expenseDate) = FUNCTION('MONTH', CURRENT_DATE)")
    List<Expense> findCurrentMonthExpenses();

    // Get top spenders (categories)
    @Query("SELECT e.category.name, SUM(e.amount) " +
            "FROM Expense e " +
            "GROUP BY e.category.name " +
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTopSpendingCategories();

    @Query("SELECT CASE WHEN SUM(e.amount) >:budget THEN true ELSE false END " +
            "FROM Expense e " +
            "WHERE e.category.id = :categoryId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate" )
    boolean isBudgetExceeded(
            @Param("categoryId") Long categoryId,
            @Param("budget") BigDecimal budget,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ========== Native SQL Queries ==========

    // Monthly summary
    @Query(value = "SELECT YEAR(e.expense_date) as year, " +
            "MONTH(e.expense_date) as month, " +
            "SUM(e.amount) as total " +
            "FROM expense e " +
            "GROUP BY year, month " + // SQL often allows aliases here
            "ORDER BY year DESC, month DESC",
            nativeQuery = true)
    List<Object[]> getMonthlySummary();

    // Complex aggregation with native SQL
    @Query(value = "SELECT " +
            "c.name as category, " +
            "COUNT(e.id) as count, " +
            "SUM(e.amount) as total, " +
            "AVG(e.amount) as average, " +
            "MIN(e.amount) as minimum, " +
            "MAX(e.amount) as maximum " +
            "FROM expenses e " +
            "JOIN categories c ON e.category_id = c.id " +
            "WHERE e.expense_date BETWEEN :startDate AND :endDate " +
            "GROUP BY c.name " +
            "ORDER BY total DESC",
            nativeQuery = true)
    List<Object[]> getDetailedCategoryStats(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Daily spending trend
    @Query(value = "SELECT e.expense_date, SUM(e.amount) " +
            "FROM expenses e " +
            "WHERE e.expense_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) " +
            "GROUP BY e.expense_date " +
            "ORDER BY e.expense_date",
            nativeQuery = true)
    List<Object[]> getDailySpendingLast30Days();

    // Year-over-year comparison
    @Query(value = "SELECT " +
            "YEAR(expense_date) as year, " +
            "MONTH(expense_date) as month, " +
            "SUM(amount) as total " +
            "FROM expenses " +
            "WHERE YEAR(expense_date) IN (:year1, :year2) " +
            "GROUP BY YEAR(expense_date), MONTH(expense_date) " +
            "ORDER BY year, month",
            nativeQuery = true)
    List<Object[]> getYearOverYearComparison(
            @Param("year1") int year1,
            @Param("year2") int year2
    );

    @Query(value = "SELECT " +
            "curr.total AS current_total, " +
            "prev.total AS previous_total, " +
            "CASE " +
            "WHEN prev.total = 0 THEN 0 " +
            "ELSE ROUND(((curr.total - prev.total) / prev.total) * 100, 2) " +
            "END AS growth_percent " +
            "FROM expense" +
            "(SELECT COALESCE(SUM(amount), 0) AS total " +
            "FROM expense " +
            "WHERE YEAR(expense_date) = :year AND MONTH(expense_date) = :month) AS curr, " +
            "(SELECT COALESCE(SUM(amount), 0) AS total " +
            "FROM expense " +
            "WHERE (MONTH(expense_date) = :month - 1 AND YEAR(expense_date) = :year) " +
            "OR (:month = 1 AND MONTH(expense_date) = 12 AND YEAR(expense_date) = :year - 1)) AS prev"
            , nativeQuery = true)
    Map<String, Object> getMonthlyGrowth(@Param("year") int year,@Param("month") int month);
}