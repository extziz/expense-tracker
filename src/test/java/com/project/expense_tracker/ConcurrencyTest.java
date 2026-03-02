package com.project.expense_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import com.project.expense_tracker.service.ExpenseService;

// 1. Use SpringBootTest so we have a REAL database and REAL service
@SpringBootTest
public class ConcurrencyTest {

    // 2. Use Autowired to bring in the real Spring Beans
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseService expenseService;

    private Category testCategory;
    private CreateExpenseRequest createRequest;

    @BeforeEach
    void setUp() {
        // Clear the database before starting
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create and SAVE a real category to the database
        testCategory = new Category();
        testCategory.setName("Food");
        testCategory.setColor("#FF5733");
        testCategory = categoryRepository.save(testCategory);

        // 3. Initialize the request
        createRequest = new CreateExpenseRequest(
                new BigDecimal("15.00"),
                "Concurrent Coffee",
                testCategory.getId(),
                LocalDate.now());
    }

    @Test
    void createExpense_whenConcurrent_shouldHandleRaceCondition() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    expenseService.createExpense(createRequest);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("Thread failed: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // FIRE THE STARTING PISTOL
        startLatch.countDown();

        // Wait for everyone to finish
        doneLatch.await();
        executorService.shutdown();

        // Assert the results against the REAL database
        long totalExpenses = expenseRepository.count();
        assertEquals(10, totalExpenses, "Exactly 10 expenses should have been created concurrently");
    }
}