package com.project.expense_tracker.dto;

import com.project.expense_tracker.exception.InvalidExpenseException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DateRangeRequest {
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    public void validate() {
        if (startDate.isAfter(endDate)) {
            throw new InvalidExpenseException("Invalid date range");
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
