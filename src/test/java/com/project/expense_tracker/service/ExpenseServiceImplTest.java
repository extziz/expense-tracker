package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.dto.ExpenseResponse;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.ExpenseNotFoundException;
import com.project.expense_tracker.mapper.ExpenseMapper;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import com.project.expense_tracker.repository.CategoryRepository;
import com.project.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Category testCategory;
    private Expense testExpense;
    private CreateExpenseRequest createRequest;
    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setColor("#FF5733");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setAmount(new BigDecimal("50.00"));
        testExpense.setDescription("Lunch");
        testExpense.setCategory(testCategory);
        testExpense.setExpenseDate(LocalDate.now());

        createRequest = new CreateExpenseRequest(
                new BigDecimal("50.00"),
                "Lunch",
                1L,
                LocalDate.now()
        );

        expenseResponse = new ExpenseResponse();
        expenseResponse.setId(1L);
        expenseResponse.setAmount(new BigDecimal("50.00"));
        expenseResponse.setDescription("Lunch");
        expenseResponse.setCategoryId(1L);
        expenseResponse.setCategoryName("Food");
    }

    @Test
    @DisplayName("Should create expense successfully")
    void createExpense_whenValidRequest_shouldCreateExpense() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(expenseMapper.toEntity(createRequest)).thenReturn(testExpense);
        when(expenseRepository.save(testExpense)).thenReturn(testExpense);
        when(expenseMapper.toResponse(testExpense)).thenReturn(expenseResponse);

        // Act
        ExpenseResponse result = expenseService.createExpense(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals("Lunch", result.getDescription());

        verify(categoryRepository).findById(1L);
        verify(expenseRepository).save(testExpense);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void createExpense_whenCategoryNotFound_shouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        CreateExpenseRequest invalidRequest = new CreateExpenseRequest(
                new BigDecimal("50.00"),
                "Lunch",
                999L,  // Non-existent category
                LocalDate.now()
        );

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> {
            expenseService.createExpense(invalidRequest);
        });

        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get expense by ID successfully")
    void getExpenseById_whenExpenseExists_shouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(expenseMapper.toResponse(testExpense)).thenReturn(expenseResponse);

        // Act
        ExpenseResponse result = expenseService.getExpenseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(expenseRepository).findById(1L);
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void deleteExpense_whenExpenseExists_shouldDelete() {
        // Arrange
        when(expenseRepository.existsById(1L)).thenReturn(true);

        // Act
        expenseService.deleteExpense(1L);

        // Assert
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent expense")
    void deleteExpense_whenExpenseNotExists_shouldThrowException() {
        // Arrange
        when(expenseRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.deleteExpense(999L);
        });

        verify(expenseRepository, never()).deleteById(any());
    }
}