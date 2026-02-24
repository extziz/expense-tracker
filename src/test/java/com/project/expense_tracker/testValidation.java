package com.project.expense_tracker;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.expense_tracker.controller.CategoryController;
import com.project.expense_tracker.controller.ExpenseController;
import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.service.CategoryService;
import com.project.expense_tracker.service.ExpenseService;

@WebMvcTest({ CategoryController.class, ExpenseController.class })
class testValidation {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void createCategory_whenNameTooShort_shouldFail() throws Exception {
        // Test validation for name < 2 characters
        CreateCategoryRequest createRequest = new CreateCategoryRequest(
                "s",
                "#FF5733",
                "food");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    void createExpense_whenAmountNegative_shouldFail() throws Exception {
        CreateExpenseRequest createRequest = new CreateExpenseRequest(
                BigDecimal.valueOf(300L),
                "Kebab",
                -1L,
                LocalDate.now());

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));
        verify(expenseService, never()).createExpense(any());
    }
}