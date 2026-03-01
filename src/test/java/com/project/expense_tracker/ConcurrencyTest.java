package com.project.expense_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.ExpenseRepository;
import com.project.expense_tracker.service.ExpenseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ConcurrencyTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Category testCategory;
    private CreateExpenseRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setColor("#FF5733");
    }

    @Test
    void createExpense_whenConcurrent_shouldHandleRaceCondition() throws InterruptedException {
        int numberOfThreads = 10;

        // 1. Create a "pool" of 10 separate threads (like 10 separate users)
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 2. Set up our Start Line and Finish Line
        CountDownLatch startLatch = new CountDownLatch(1); // The starting pistol
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads); // The finish line

        // 3. Queue up the 10 tasks
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // ALL 10 threads hit this line and freeze, waiting for the startLatch to drop
                    // to 0
                    startLatch.await();

                    // --- THE ACTUAL ACTION ---
                    // Create and save the expense.
                    // Note: If you have an ExpenseService, call it here instead of the repository.
                    Expense concurrentExpense = expenseService.createExpense(createRequest);
                    expenseRepository.save(concurrentExpense);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // As each thread finishes saving, it crosses the finish line
                    doneLatch.countDown();
                }
            });
        }

        // 4. FIRE THE STARTING PISTOL!
        // This drops the startLatch to 0. All 10 frozen threads instantly wake up and
        // try to save at the same time.
        startLatch.countDown();

        // 5. Wait for everyone to finish
        // The main test pauses here until all 10 threads have called
        // doneLatch.countDown()
        doneLatch.await();
        executorService.shutdown();

        // 6. Assert the results
        // If the database handled the concurrency correctly, there should be exactly 10
        // new records.
        long totalExpenses = expenseRepository.count();
        assertEquals(10, totalExpenses, "Exactly 10 expenses should have been created concurrently");
    }
}
