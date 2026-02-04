package com.project.expense_tracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockitoBasicsTest {

    @Mock
    private List<String> mockList;

    @Test
    void testMockBehavior() {
        // Define behavior: when this happens, return that
        when(mockList.size()).thenReturn(5);
        when(mockList.get(0)).thenReturn("First");

        // Use the mock
        assertEquals(5, mockList.size());
        assertEquals("First", mockList.get(0));

        // Verify interactions
        verify(mockList).size();
        verify(mockList).get(0);
    }

    @Test
    void testMockWithArgMatchers() {
        // any() matcher - matches any argument
        when(mockList.get(anyInt())).thenReturn("Any position");

        assertEquals("Any position", mockList.get(0));
        assertEquals("Any position", mockList.get(100));

        // Specific value
        when(mockList.get(0)).thenReturn("Specific");
        assertEquals("Specific", mockList.get(0));
        assertEquals("Any position", mockList.get(1));
    }

    @Test
    void testExceptionThrowing() {
        // Make mock throw exception
        when(mockList.get(anyInt()))
                .thenThrow(new IndexOutOfBoundsException("Invalid index"));

        assertThrows(IndexOutOfBoundsException.class, () -> {
            mockList.get(0);
        });
    }

    @Test
    void testVerifyInteractions() {
        mockList.add("One");
        mockList.add("Two");
        mockList.clear();

        // Verify method was called
        verify(mockList).add("One");
        verify(mockList).add("Two");
        verify(mockList).clear();

        // Verify call count
        verify(mockList, times(2)).add(anyString());
        verify(mockList, times(1)).clear();

        // Never called
        verify(mockList, never()).remove(anyString());
    }
}