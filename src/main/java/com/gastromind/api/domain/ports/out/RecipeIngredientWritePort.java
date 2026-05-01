package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.RecipeIngredientUsage;

import java.util.List;

/**
 * Define el contrato de persistencia o integracion para recipe ingredient write.
 */
public interface RecipeIngredientWritePort {

    void saveForRecipe(String recipeId, List<RecipeIngredientUsage> usages);
}
