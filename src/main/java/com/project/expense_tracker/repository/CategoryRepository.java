package com.project.expense_tracker.repository;

import com.project.expense_tracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ========== Derived Query Methods ==========

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findByNameContaining(String keyword);

    List<Category> findByNameContainingIgnoreCase(String keyword);

    List<Category> findByColorStartingWith(String colorPrefix);

    // Find categories with description
    List<Category> findByDescriptionIsNotNull();

    // Find categories ordered by name
    List<Category> findAllByOrderByNameAsc();

    // Find categories by multiple colors
    List<Category> findByColorIn(List<String> colors);

    // ========== Custom JPQL Queries ==========

    // Count expenses per category
    @Query("SELECT c FROM Category c WHERE SIZE(c.expenses) > :minCount")
    List<Category> findCategoriesWithMinimumExpenses(@Param("minCount") int minCount);

    // Find categories with no expenses
    @Query("SELECT c FROM Category c WHERE c.expenses IS EMPTY")
    List<Category> findCategoriesWithoutExpenses();

    // Search by name or description
    @Query("SELECT c FROM Category c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByNameOrDescription(@Param("keyword") String keyword);

    // ========== Native SQL Queries ==========

    // Get category usage statistics
    @Query(value = "SELECT c.id, c.name, COUNT(e.id) as expense_count, " +
            "COALESCE(SUM(e.amount), 0) as total_amount " +
            "FROM categories c " +
            "LEFT JOIN expenses e ON c.id = e.category_id " +
            "GROUP BY c.id, c.name " +
            "ORDER BY total_amount DESC",
            nativeQuery = true)
    List<Object[]> getCategoryStatistics();
}