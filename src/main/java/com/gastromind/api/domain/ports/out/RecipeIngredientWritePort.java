package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.RecipeIngredientUsage;

import java.util.List;

/**
 * Persiste los ingredientes desglosados de una receta tras generarla o importarla.
 */
public interface RecipeIngredientWritePort {

    void saveForRecipe(String recipeId, List<RecipeIngredientUsage> usages);
}
