package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.IngredientUsageResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de recipe rest.
 */
public interface RecipeRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingredientsUsed", ignore = true)
    Recipe toDomain(RecipeRequest request);

    @Mapping(target = "ingredientsUsed", source = "ingredientsUsed", qualifiedByName = "toIngredientUsageResponses")
    RecipeResponse toResponse(Recipe domain);

    List<RecipeResponse> toResponseList(List<Recipe> recipes);

    @Named("toIngredientUsageResponses")
    default List<IngredientUsageResponse> toIngredientUsageResponses(List<RecipeIngredientUsage> usages) {
        if (usages == null || usages.isEmpty()) {
            return Collections.emptyList();
        }
        return usages.stream()
                .map(u -> new IngredientUsageResponse(
                        u.getProductId(),
                        u.getProductName(),
                        u.getQuantityUsed(),
                        u.getQuantityAvailable()))
                .collect(Collectors.toList());
    }
}






