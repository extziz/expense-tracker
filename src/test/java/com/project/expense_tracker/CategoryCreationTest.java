package com.project.expense_tracker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.mapper.CategoryMapper;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.service.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryCreationTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryMapper categoryMapper;

    @InjectMocks
    CategoryServiceImpl categoryService;

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
}