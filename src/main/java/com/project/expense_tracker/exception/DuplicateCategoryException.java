package com.project.expense_tracker.exception;

public class DuplicateCategoryException extends RuntimeException {

    public DuplicateCategoryException(String name) {
        super("Category with name '" + name + "' already exists");
    }
}