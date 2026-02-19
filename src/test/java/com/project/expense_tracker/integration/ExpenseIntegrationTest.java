package com.project.expense_tracker.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.expense_tracker.dto.CategoryResponse;
import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.dto.ExpenseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use application-test.yml
@Transactional // Rollback after each test
class ExpenseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception {
        // Create a category first
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest(
                "Food",
                "#FF5733",
                "Food expenses");

        MvcResult result = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponse category = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponse.class);
        categoryId = category.getId();
    }

    @Test
    @DisplayName("Full expense lifecycle - create, read, update, delete")
    void fullExpenseLifecycle() throws Exception {
        // 1. CREATE expense
        CreateExpenseRequest createRequest = new CreateExpenseRequest(
                new BigDecimal("50.00"),
                "Lunch at restaurant",
                categoryId,
                LocalDate.now());

        MvcResult createResult = mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(50.00)))
                .andExpect(jsonPath("$.description", is("Lunch at restaurant")))
                .andExpect(jsonPath("$.categoryName", is("Food")))
                .andReturn();

        ExpenseResponse expense = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ExpenseResponse.class);
        Long expenseId = expense.getId();

        // 2. READ expense
        mockMvc.perform(get("/api/expenses/" + expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expenseId.intValue())))
                .andExpect(jsonPath("$.amount", is(50.00)));

        // 3. UPDATE expense
        CreateExpenseRequest updateRequest = new CreateExpenseRequest(
                new BigDecimal("55.00"), // Changed amount
                "Lunch at restaurant",
                categoryId,
                LocalDate.now());

        mockMvc.perform(put("/api/expenses/" + expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(55.00)));

        // 4. DELETE expense
        mockMvc.perform(delete("/api/expenses/" + expenseId))
                .andExpect(status().isNoContent());

        // 5. Verify deletion
        mockMvc.perform(get("/api/expenses/" + expenseId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter expenses by category")
    void filterExpensesByCategory() throws Exception {
        // Create multiple expenses
        CreateExpenseRequest expense1 = new CreateExpenseRequest(
                new BigDecimal("50.00"),
                "Lunch",
                categoryId,
                LocalDate.now());

        CreateExpenseRequest expense2 = new CreateExpenseRequest(
                new BigDecimal("120.00"),
                "Groceries",
                categoryId,
                LocalDate.now());

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense2)))
                .andExpect(status().isCreated());

        // Filter by category
        mockMvc.perform(get("/api/expenses/category/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}