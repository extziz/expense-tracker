package com.project.expense_tracker.mapper;

import com.project.expense_tracker.dto.*;
import com.project.expense_tracker.model.Expense;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    // Entity to Response DTO (with custom mappings for category fields)
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.color", target = "categoryColor")
    ExpenseResponse toResponse(Expense expense);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.color", target = "categoryColor")
    ExpenseResponse2 toResponse2(Expense expense);

    // List conversion
    List<ExpenseSummaryResponse> toResponseList(List<Expense> expenses);

    List<ExpenseResponse2> toResponse2List(List<Expense> expenses);

    // Request DTO to Entity (ignore category, we'll set it separately)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Expense toEntity(CreateExpenseRequest request);

    // Update entity from request (ignore null values and certain fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateExpenseRequest request, @MappingTarget Expense expense);

    default void calculateFields(@MappingTarget ExpenseResponse2 response, Expense expense) {
        String formattedAmount = "$" + expense.getAmount();
        long dayDifference = ChronoUnit.DAYS.between(expense.getExpenseDate(), LocalDate.now());
        String relativeDate = dayDifference + " days ago";

        response.setFormattedAmount(formattedAmount);
        response.setRelativeDate(relativeDate);
    }
}
