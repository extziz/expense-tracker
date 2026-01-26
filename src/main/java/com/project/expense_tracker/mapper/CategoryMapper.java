package com.project.expense_tracker.mapper;

import com.project.expense_tracker.dto.CategoryResponse;
import com.project.expense_tracker.dto.CategorySummaryResponse;
import com.project.expense_tracker.dto.CreateCategoryRequest;
import com.project.expense_tracker.dto.UpdateCategoryRequest;
import com.project.expense_tracker.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity to Response DTO
    CategoryResponse toResponse(Category category);

    // Entity to Summary DTO
    CategorySummaryResponse toSummary(Category category);

    // List conversion
    List<CategoryResponse> toResponseList(List<Category> categories);
    List<CategorySummaryResponse> toSummaryList(List<Category> categories);

    // Request DTO to Entity
    Category toEntity(CreateCategoryRequest request);

    // Update entity from request (ignore null values)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCategoryRequest request, @MappingTarget Category category);
}