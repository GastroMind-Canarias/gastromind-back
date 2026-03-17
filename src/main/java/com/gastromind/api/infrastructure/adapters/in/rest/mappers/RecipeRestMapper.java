package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeRestMapper {
    @Mapping(target = "id", ignore = true)
    Recipe toDomain(RecipeRequest request);

    RecipeResponse toResponse(Recipe domain);

    List<RecipeResponse> toResponseList(List<Recipe> recipes);
}