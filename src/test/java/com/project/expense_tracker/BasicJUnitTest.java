package com.project.expense_tracker;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BasicJUnitTest {

    // Runs once before all tests in this class
    @BeforeAll
    static void setupAll() {
        System.out.println("Setting up test suite");
    }

    // Runs before each test
    @BeforeEach
    void setupEach() {
        System.out.println("Setting up test");
    }

    // A simple test
    @Test
    void testAddition() {
        int result = 2 + 2;
        assertEquals(4, result);
    }

    // Test with display name
    @Test
    @DisplayName("Should multiply two numbers correctly")
    void testMultiplication() {
        int result = 3 * 4;
        assertEquals(12, result, "3 * 4 should equal 12");
    }

    // Test exceptions
    @Test
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        });
    }

    // Test with timeout
    @Test
    @Timeout(1)  // Must complete within 1 second
    void testPerformance() {
        // Some operation
    }

    // Disabled test
    @Test
    @Disabled("Not implemented yet")
    void testFutureFeature() {
        fail("This test is not ready");
    }

    // Runs after each test
    @AfterEach
    void teardownEach() {
        System.out.println("Cleaning up after test");
    }

    // Runs once after all tests
    @AfterAll
    static void teardownAll() {
        System.out.println("Cleaning up test suite");
    }
}