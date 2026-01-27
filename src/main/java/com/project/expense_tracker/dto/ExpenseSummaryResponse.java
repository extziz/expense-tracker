package com.project.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseSummaryResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private String categoryName;
    private LocalDate expenseDate;

    public ExpenseSummaryResponse(){}

    public ExpenseSummaryResponse(Long id, BigDecimal amount, String description, String categoryName, LocalDate expenseDate){
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.categoryName = categoryName;
        this.expenseDate = expenseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


