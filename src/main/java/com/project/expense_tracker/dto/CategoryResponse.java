package com.project.expense_tracker.dto;

import java.time.LocalDateTime;

public class CategoryResponse {

    private Long id;
    private String name;
    private String color;
    private String description;
    private LocalDateTime createdAt;

    // Constructors
    public CategoryResponse() {}

    public CategoryResponse(Long id, String name, String color, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.createdAt = createdAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}