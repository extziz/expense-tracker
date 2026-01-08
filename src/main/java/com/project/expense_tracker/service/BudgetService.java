package com.project.expense_tracker.service;

import com.project.expense_tracker.model.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface BudgetService {

    Budget setBudget(Long categoryId, YearMonth month, BigDecimal limit);

    boolean isBudgetExceeded(Long categoryId, YearMonth month);

    BigDecimal getRemainingBudget(Long categoryId, YearMonth month);
}
