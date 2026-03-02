package com.project.expense_tracker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;

import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.service.CategoryService;

@SpringBootTest
public class CategoryCreationTest {

    @Autowired
    CategoryService categoryService;

    CreateCategoryRequest createRequest;

    @ParameterizedTest
    @CsvSource({
            "Food, #FF5733, true",
            "F, #FF5733, false",
            "Food, red, false",
            "'', #FF5733, false"
    })
    void validateCategoryCreation(String name, String color, boolean shouldPass) {

        createRequest = new CreateCategoryRequest(
                name,
                color,
                "Food expenses");

        if (shouldPass) {
            assertDoesNotThrow(() -> categoryService.createCategory(createRequest));
        } else {
            assertThrows(Exception.class, () -> categoryService.createCategory(createRequest));
        }
    }

    @Test
    void verifyCategoryColorFormat() {
        Category myCategory = new Category("Food", "#FF5733", "");

        assertThat(myCategory, CategoryMatcher.hasValidColor());
    }
}