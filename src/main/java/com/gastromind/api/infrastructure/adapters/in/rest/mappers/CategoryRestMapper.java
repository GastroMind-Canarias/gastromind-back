package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {
    @Mapping(target = "id", ignore = true)
    Category toDomain(CategoryRequest request);

    CategoryResponse toResponse(Category domain);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
