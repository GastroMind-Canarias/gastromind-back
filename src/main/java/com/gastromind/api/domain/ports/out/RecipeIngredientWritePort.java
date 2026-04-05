package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.RecipeIngredientUsage;

import java.util.List;

/**
 * Persistencia de líneas de ingredientes asociadas a una receta ya guardada.
 */
public interface RecipeIngredientWritePort {

    void saveForRecipe(String recipeId, List<RecipeIngredientUsage> usages);
}
