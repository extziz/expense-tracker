package com.project.expense_tracker.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ExpenseDateValidator implements ConstraintValidator<ValidExpenseDate, LocalDate>{

    @Override
    public boolean isValid(LocalDate dateOfCreation, ConstraintValidatorContext context){
        if (dateOfCreation == null) {
            return true;
        }

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);

        return dateOfCreation.isAfter(oneYearAgo) && !dateOfCreation.isAfter(today);
    }
}
