package com.project.expense_tracker.repository;

import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category foodCategory;
    private Expense expense1;
    private Expense expense2;

    @BeforeEach
    void setUp() {
        // Clear database
        expenseRepository.deleteAll();

        // Create category
        foodCategory = new Category("Food", "#FF5733", "Food");
        entityManager.persist(foodCategory);

        // Create expenses
        expense1 = new Expense(
                new BigDecimal("50.00"),
                "Lunch at restaurant",
                foodCategory,
                LocalDate.of(2024, 12, 15));

        expense2 = new Expense(
                new BigDecimal("120.00"),
                "Groceries",
                foodCategory,
                LocalDate.of(2024, 12, 20));

        entityManager.persist(expense1);
        entityManager.persist(expense2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find expenses by category ID")
    void findByCategory_Id_shouldReturnExpensesForCategory() {
        // Act
        List<Expense> result = expenseRepository.findByCategory_Id(foodCategory.getId());

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should find expenses by date range")
    void findByExpenseDateBetween_shouldReturnExpensesInRange() {
        // Act
        LocalDate start = LocalDate.of(2024, 12, 1);
        LocalDate end = LocalDate.of(2024, 12, 18);
        List<Expense> result = expenseRepository.findByExpenseDateBetween(start, end);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Lunch at restaurant", result.get(0).getDescription());
    }

    @Test
    @DisplayName("Should find expenses by amount greater than")
    void findByAmountGreaterThan_shouldReturnExpensivExpenses() {
        // Act
        List<Expense> result = expenseRepository.findByAmountGreaterThan(
                new BigDecimal("100.00"));

        // Assert
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("120.00"), result.get(0).getAmount());
    }

    @Test
    @DisplayName("Should find expenses by description containing keyword")
    void findByDescriptionContainingIgnoreCase_shouldReturnMatches() {
        // Act
        List<Expense> result = expenseRepository
                .findByDescriptionContainingIgnoreCase("lunch");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Lunch at restaurant", result.get(0).getDescription());
    }

    @Test
    @DisplayName("Should get top expenses ordered by amount")
    void findTop10ByOrderByAmountDesc_shouldReturnTopExpenses() {
        // Act
        List<Expense> result = expenseRepository.findTop10ByOrderByAmountDesc();

        // Assert
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("120.00"), result.get(0).getAmount());
        assertEquals(new BigDecimal("50.00"), result.get(1).getAmount());
    }
}