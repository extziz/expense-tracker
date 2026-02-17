package com.project.expense_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.expense_tracker.dto.CategoryResponse;
import com.project.expense_tracker.dto.CategorySummaryResponse;
import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
import com.project.expense_tracker.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategorySummaryResponse categorySummaryResponse;
    private CategoryResponse categoryResponse;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse(
                1L,
                "Food",
                "#FF5733",
                "Food expenses",
                LocalDateTime.now());

        categorySummaryResponse = new CategorySummaryResponse(
                1L,
                "Food",
                "#FF5733");

        createRequest = new CreateCategoryRequest(
                "Food",
                "#FF5733",
                "Food expenses");
    }

    @Test
    @DisplayName("GET /api/categories should return all categories")
    void getAllCategories_shouldReturnCategoriesList() throws Exception {
        // Arrange
        List<CategorySummaryResponse> categories = Arrays.asList(categorySummaryResponse);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Food")))
                .andExpect(jsonPath("$[0].color", is("#FF5733")));

        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/categories/{id} should return category")
    void getCategoryById_whenExists_shouldReturnCategory() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Food")))
                .andExpect(jsonPath("$.color", is("#FF5733")));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @DisplayName("GET /api/categories/{id} should return 404 when not found")
    void getCategoryById_whenNotExists_shouldReturn404() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999L))
                .thenThrow(new CategoryNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    @DisplayName("POST /api/categories should create category")
    void createCategory_whenValid_shouldReturnCreated() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CreateCategoryRequest.class)))
                .thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Food")));

        verify(categoryService).createCategory(any(CreateCategoryRequest.class));
    }

    @Test
    @DisplayName("POST /api/categories should return 400 for invalid data")
    void createCategory_whenInvalid_shouldReturn400() throws Exception {
        // Arrange - invalid request (name too short)
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest(
                "F", // Too short
                "#FF5733",
                "Food");

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    @DisplayName("POST /api/categories should return 409 for duplicate")
    void createCategory_whenDuplicate_shouldReturn409() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CreateCategoryRequest.class)))
                .thenThrow(new DuplicateCategoryException("Food"));

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Food")));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} should return 204")
    void deleteCategory_whenExists_shouldReturn204() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }
}