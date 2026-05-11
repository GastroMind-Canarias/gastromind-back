package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Recipe;

/**
 * Genera una receta usando el modelo externo a partir del stock y restricciones del hogar.
 */
public interface RecipeAiPort {

    Recipe generateOneRecipe(HouseholdRecipeContext context);
}
