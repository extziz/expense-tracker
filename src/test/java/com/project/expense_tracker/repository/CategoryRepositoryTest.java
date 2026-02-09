package com.project.expense_tracker.repository;

import com.project.expense_tracker.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest  // Only loads JPA components, uses in-memory H2
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category foodCategory;
    private Category transportCategory;

    @BeforeEach
    void setUp() {
        // Clear database
        categoryRepository.deleteAll();

        // Create test data
        foodCategory = new Category("Food", "#FF5733", "Food expenses");
        transportCategory = new Category("Transport", "#3357FF", "Transport expenses");

        // Persist test data
        entityManager.persist(foodCategory);
        entityManager.persist(transportCategory);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find category by name")
    void findByName_whenCategoryExists_shouldReturnCategory() {
        // Act
        Optional<Category> result = categoryRepository.findByName("Food");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
        assertEquals("#FF5733", result.get().getColor());
    }

    @Test
    @DisplayName("Should return empty when category not found by name")
    void findByName_whenCategoryNotExists_shouldReturnEmpty() {
        // Act
        Optional<Category> result = categoryRepository.findByName("NonExistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should check if category exists by name")
    void existsByName_shouldReturnCorrectBoolean() {
        // Act & Assert
        assertTrue(categoryRepository.existsByName("Food"));
        assertFalse(categoryRepository.existsByName("NonExistent"));
    }

    @Test
    @DisplayName("Should find categories by name containing keyword")
    void findByNameContaining_shouldReturnMatchingCategories() {
        // Act
        List<Category> result = categoryRepository.findByNameContaining("or");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Transport", result.get(0).getName());
    }

    @Test
    @DisplayName("Should find all categories ordered by name")
    void findAllByOrderByNameAsc_shouldReturnOrderedList() {
        // Act
        List<Category> result = categoryRepository.findAllByOrderByNameAsc();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("Transport", result.get(1).getName());
    }

    @Test
    @DisplayName("Should save category successfully")
    void save_shouldPersistCategory() {
        // Arrange
        Category newCategory = new Category("Entertainment", "#FF33FF", "Entertainment");

        // Act
        Category saved = categoryRepository.save(newCategory);

        // Assert
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());

        // Verify it's in database
        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Entertainment", found.get().getName());
    }

    @Test
    @DisplayName("Should delete category successfully")
    void delete_shouldRemoveCategoryFromDatabase() {
        // Arrange
        Long categoryId = foodCategory.getId();

        // Act
        categoryRepository.deleteById(categoryId);

        // Assert
        Optional<Category> result = categoryRepository.findById(categoryId);
        assertFalse(result.isPresent());
    }
}