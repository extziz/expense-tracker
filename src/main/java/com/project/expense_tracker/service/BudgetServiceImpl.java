package com.project.expense_tracker.service;

import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.model.Budget;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.repository.BudgetRepository;
import com.project.expense_tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BudgetServiceImpl(BudgetRepository budgetRepository, CategoryRepository categoryRepository){
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Budget setBudget(Long categoryId, YearMonth month, BigDecimal limit){
        Budget budget = new Budget(categoryId, limit, month);
        return budgetRepository.save(budget);
    }


}
