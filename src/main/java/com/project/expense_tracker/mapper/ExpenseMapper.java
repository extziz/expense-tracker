package com.project.expense_tracker.mapper;

import com.project.expense_tracker.dto.CreateExpenseRequest;
import com.project.expense_tracker.dto.ExpenseResponse;
import com.project.expense_tracker.dto.UpdateExpenseRequest;
import com.project.expense_tracker.model.Expense;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    // Entity to Response DTO (with custom mappings for category fields)
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.color", target = "categoryColor")
    ExpenseResponse toResponse(Expense expense);

    // List conversion
    List<ExpenseResponse> toResponseList(List<Expense> expenses);

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
}