package com.project.expense_tracker.exception;

public enum ErrorCode {
    CAT_001("CAT_001", "Invalid category name"),
    CAT_002("CAT_002", "Category already exists"),
    CAT_003("CAT_003", "Category not found");

    private final String code;
    private final String suggestion;

    // Constructor
    ErrorCode(String code, String suggestion) {
        this.code = code;
        this.suggestion = suggestion;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getSuggestion() {
        return suggestion;
    }
}