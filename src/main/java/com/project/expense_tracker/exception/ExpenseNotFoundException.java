package com.project.expense_tracker.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense with ID " + id + " not found");
    }

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}