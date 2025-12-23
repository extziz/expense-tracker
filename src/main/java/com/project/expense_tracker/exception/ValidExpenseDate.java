package com.project.expense_tracker.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD,  ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExpenseDateValidator.class)
public @interface ValidExpenseDate {

    String message() default "Expense date must be not more than 1 year in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}