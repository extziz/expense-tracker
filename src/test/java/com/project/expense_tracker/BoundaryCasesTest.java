package com.project.expense_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.ExpenseRepository;

@DataJpaTest
public class BoundaryCasesTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category foodCategory;
    private Expense expense1;
    private Expense expense2;
    private Expense expense3;
    private Expense expense4;

    @BeforeEach
    void setUp() {
        // Clear database
        expenseRepository.deleteAll();

        // Create category
        foodCategory = new Category("Food", "#FF5733", "Food");
        entityManager.persist(foodCategory);

        // Create expenses
        expense1 = new Expense(
                new BigDecimal("100.00"),
                "Lunch at restaurant",
                foodCategory,
                LocalDate.of(2024, 12, 15));

        expense2 = new Expense(
                new BigDecimal("101.00"),
                "Groceries",
                foodCategory,
                LocalDate.of(2024, 12, 20));

        expense3 = new Expense(
                new BigDecimal("99999998.99"),
                "Lunch",
                foodCategory,
                LocalDate.of(2024, 12, 15));

        expense4 = new Expense(
                BigDecimal.ZERO,
                "Shop",
                foodCategory,
                LocalDate.of(2024, 12, 20));

        entityManager.persist(expense1);
        entityManager.persist(expense2);
        entityManager.persist(expense3);
        entityManager.persist(expense4);
        entityManager.flush();
    }

    @Test
    void findByAmountBetween_withEdgeCases() {

        // min = max
        List<Expense> result1 = expenseRepository.findByAmountBetween(BigDecimal.valueOf(100), BigDecimal.valueOf(100));
        assertEquals(1, result1.size());
        assertEquals("Lunch at restaurant", result1.get(0).getDescription());

        // large amounts
        List<Expense> result2 = expenseRepository.findByAmountBetween(BigDecimal.valueOf(99999997.98),
                BigDecimal.valueOf(99999999.99));
        assertEquals(1, result2.size());
        assertEquals("Lunch", result2.get(0).getDescription());

        // zero amounts
        List<Expense> result3 = expenseRepository.findByAmountBetween(BigDecimal.ZERO, BigDecimal.valueOf(0.5));
        assertEquals(1, result3.size());
        assertEquals("Shop", result3.get(0).getDescription());
    }
}
