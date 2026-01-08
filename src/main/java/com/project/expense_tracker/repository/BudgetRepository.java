package com.project.expense_tracker.repository;

import com.project.expense_tracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByCategoryIdAndMonth(Long categoryId, YearMonth month);
}
