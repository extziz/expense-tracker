package com.project.expense_tracker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.project.expense_tracker.model.Category;

public class CategoryMatcher {
    public static Matcher<Category> hasValidColor() {
        return new TypeSafeMatcher<Category>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("category with valid hex color");
            }

            @Override
            protected boolean matchesSafely(Category category) {
                return category.getColor().matches("^#[0-9A-Fa-f]{6}$");
            }
        };
    }
}