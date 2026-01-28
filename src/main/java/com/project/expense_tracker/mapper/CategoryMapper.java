package com.project.expense_tracker.mapper;

import com.project.expense_tracker.dto.*;
import com.project.expense_tracker.model.Category;
import com.project.expense_tracker.model.Expense;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity to Response DTO
    CategoryResponse toResponse(Category category);

    // Entity to Summary DTO
    CategorySummaryResponse toSummary(Category category);

    default CategoryWithStatsResponse toStatsResponse(Category category){
        if (category == null){
            return null;
        }
        List<Expense> expenses = category.getExpenses();
        int expenseCount = (expenses == null)? 0 : expenses.size();

        BigDecimal totalAmount = (expenses == null)
        ? BigDecimal.ZERO
        :expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CategoryWithStatsResponse(
                category.getId(),
                category.getName(),
                category.getColor(),
                expenseCount,
                totalAmount
                );
    }

    // List conversion
    List<CategoryResponse> toResponseList(List<Category> categories);
    List<CategorySummaryResponse> toSummaryList(List<Category> categories);
    // Request DTO to Entity
    Category toEntity(CreateCategoryRequest request);

    // Update entity from request (ignore null values)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCategoryRequest request, @MappingTarget Category category);
}