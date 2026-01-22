package com.project.expense_tracker.service;
import com.project.expense_tracker.model.Expense;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

    public class ExpenseSpecifications {

        // Static method for filtering by Category
        public static Specification<Expense> hasCategory(Long categoryId) {
            return (root, query, cb) -> {
                return cb.equal(root.get("categoryId"), categoryId);
            };
        }

        // Static method for filtering by Minimum Amount
        public static Specification<Expense> priceGreaterThan(BigDecimal minAmount) {
            return (root, query, cb) -> {
                return cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
            };
        }

    }

