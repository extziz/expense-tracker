package com.project.expense_tracker.exception;

public class BudgetExceededException extends RuntimeException{
    public BudgetExceededException() {
        super("Adding this expense exceeds a monthly budget of $5000");
    }
}
