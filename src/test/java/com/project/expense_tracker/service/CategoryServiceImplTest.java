package com.project.expense_tracker.service;

import com.project.expense_tracker.dto.CategoryResponse;
import com.project.expense_tracker.dto.CategorySummaryResponse;
import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.dto.UpdateCategoryRequest;
import com.project.expense_tracker.exception.CategoryNotFoundException;
import com.project.expense_tracker.exception.DuplicateCategoryException;
import com.project.expense_tracker.mapper.CategoryMapper;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryResponse testCategoryResponse;
    private CategorySummaryResponse testCategorySummaryResponse;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        // Prepare test data
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setColor("#FF5733");
        testCategory.setDescription("Food expenses");
        testCategory.setCreatedAt(LocalDateTime.now());

        testCategoryResponse = new CategoryResponse(
                1L,
                "Food",
                "#FF5733",
                "Food expenses",
                LocalDateTime.now()
        );

        testCategorySummaryResponse = new CategorySummaryResponse(
                1L,
                "Food",
                "#FF5733"
        );

        createRequest = new CreateCategoryRequest(
                "Food",
                "#FF5733",
                "Food expenses"
        );
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void getAllCategories_shouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(testCategory);
        List<CategorySummaryResponse> summaryResponses = Arrays.asList(testCategorySummaryResponse);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toSummaryList(categories)).thenReturn(summaryResponses);

        // Act
        List<CategorySummaryResponse> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getName());

        // Verify interactions
        verify(categoryRepository).findAll();
        verify(categoryMapper).toSummaryList(categories);
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void getCategoryById_whenCategoryExists_shouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Food", result.getName());

        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toResponse(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void getCategoryById_whenCategoryNotExists_shouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategoryById(999L);
        });

        verify(categoryRepository).findById(999L);
        verify(categoryMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_whenValidRequest_shouldCreateCategory() {
        // Arrange
        when(categoryRepository.existsByName("Food")).thenReturn(false);
        when(categoryMapper.toEntity(createRequest)).thenReturn(testCategory);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Food", result.getName());

        verify(categoryRepository).existsByName("Food");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate category")
    void createCategory_whenDuplicateName_shouldThrowException() {
        // Arrange
        when(categoryRepository.existsByName("Food")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateCategoryException.class, () -> {
            categoryService.createCategory(createRequest);
        });

        verify(categoryRepository).existsByName("Food");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should set default color when not provided")
    void createCategory_whenNoColor_shouldSetDefaultColor() {
        // Arrange
        CreateCategoryRequest requestNoColor = new CreateCategoryRequest(
                "Food",
                null,  // No color
                "Food expenses"
        );

        Category categoryNoColor = new Category("Food", null, "Food expenses");

        when(categoryRepository.existsByName("Food")).thenReturn(false);
        when(categoryMapper.toEntity(requestNoColor)).thenReturn(categoryNoColor);
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryNoColor);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(testCategoryResponse);

        // Act
        categoryService.createCategory(requestNoColor);

        // Assert - verify default color was set
        verify(categoryRepository).save(argThat(category ->
                "#808080".equals(category.getColor())
        ));
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_whenValidRequest_shouldUpdateCategory() {
        // Arrange
        UpdateCategoryRequest updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Food");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Updated Food")).thenReturn(false);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(categoryMapper).updateEntityFromRequest(updateRequest, testCategory);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void deleteCategory_whenCategoryExists_shouldDelete() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void deleteCategory_whenCategoryNotExists_shouldThrowException() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.deleteCategory(999L);
        });

        verify(categoryRepository, never()).deleteById(any());
    }
}