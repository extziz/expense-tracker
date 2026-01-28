package com.project.expense_tracker.dto;

import java.math.BigDecimal;

public class CategoryWithStatsResponse {
    private Long id;
    private String name;
    private String color;
    private int expenseCount;
    private BigDecimal totalAmount;

    public CategoryWithStatsResponse(){}
    public CategoryWithStatsResponse(Long id, String name, String color, int expenseCount, BigDecimal totalAmount){
        this.id = id;
        this.name = name;
        this.color= color;
        this.expenseCount = expenseCount;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getExpenseCount() {
        return expenseCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setExpenseCount(int expenseCount) {
        this.expenseCount = expenseCount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

