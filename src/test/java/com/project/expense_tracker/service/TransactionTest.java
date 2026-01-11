package com.project.expense_tracker.service;

import com.project.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class TransactionTest {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    public void testTransactionRollback() {
        long countBefore = expenseRepository.count();
        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpenseWithIntentionalError();
        });

        long countAfter = expenseRepository.count();

        assertEquals(countBefore, countAfter, "Transaction should have rolled back!");
    }
}
