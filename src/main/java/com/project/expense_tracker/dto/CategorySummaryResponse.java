package com.project.expense_tracker.dto;

public class CategorySummaryResponse {

    private Long id;
    private String name;
    private String color;

    // Constructors
    public CategorySummaryResponse() {}

    public CategorySummaryResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}