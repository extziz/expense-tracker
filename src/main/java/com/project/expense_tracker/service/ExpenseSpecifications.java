package com.project.expense_tracker.service;
import com.project.expense_tracker.model.Expense;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseSpecifications {

        // Static method for filtering by Category
        public static Specification<Expense> hasCategory(Long categoryId) {
            return (root, query, cb) -> {
                return cb.equal(root.get("category"), categoryId);
            };
        }

        // Static method for filtering by Minimum Amount
        public static Specification<Expense> priceGreaterThan(BigDecimal minAmount) {
            return (root, query, cb) -> {
                return cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
            };
        }

        public static Specification<Expense> priceLessThan(BigDecimal maxAmount) {
            return (root, query, cb) -> {
                return cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
            };
        }

        public static Specification<Expense> dateAfterThe(LocalDate startDate) {
            return (root, query, cb) -> {
                return cb.greaterThanOrEqualTo(root.get("expenseDate"), startDate);
            };
        }

        public static Specification<Expense> dateBeforeThe(LocalDate endDate) {
            return (root, query, cb) -> {
                return cb.lessThanOrEqualTo(root.get("expenseDate"), endDate);
            };
        }

        public static Specification<Expense> containsKeyword(String keyword) {
            return (root, query, cb) -> {
                return cb.like(
                        cb.lower(root.get("description")),
                        "%" + keyword.toLowerCase() + "%"
                );
           };
        }
    }

