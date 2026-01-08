package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.BudgetExceededException;
import com.project.expense_tracker.exception.BudgetNotFoundException;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.model.Budget;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.BudgetRepository;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public BudgetServiceImpl(BudgetRepository budgetRepository, CategoryRepository categoryRepository, ExpenseRepository expenseRepository){
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Budget setBudget(Long categoryId, YearMonth month, BigDecimal limit){
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        Budget budget = budgetRepository.findByCategoryIdAndMonth(categoryId, month)
                .orElse(new Budget(categoryId, limit, month));

        budget.setMonthlyLimit(limit);

        return budgetRepository.save(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBudgetExceeded(Long categoryId, YearMonth month){
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        Budget budget = budgetRepository.findByCategoryIdAndMonth(categoryId, month)
                .orElseThrow(() -> new BudgetNotFoundException("There is no budget at " + month.toString()));
        BigDecimal totalSpent = expenseRepository.findByCategory_Id(categoryId).stream()
                .filter(e -> YearMonth.from(e.getExpenseDate()).equals(month))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalSpent.compareTo(budget.getMonthlyLimit()) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRemainingBudget(Long categoryId, YearMonth month){
        if(isBudgetExceeded(categoryId, month)){
            throw new BudgetExceededException();
        }
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        Budget budget = budgetRepository.findByCategoryIdAndMonth(categoryId, month)
                .orElseThrow(() -> new BudgetNotFoundException("There is no budget at " + month.toString()));
        BigDecimal totalSpent = expenseRepository.findByCategory_Id(categoryId).stream()
                .filter(e -> YearMonth.from(e.getExpenseDate()).equals(month))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return budget.getMonthlyLimit().subtract(totalSpent);
    }
}
