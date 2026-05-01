package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Recipe;

/**
 * Define el contrato de persistencia o integracion para recipe ai.
 */
public interface RecipeAiPort {

    Recipe generateOneRecipe(HouseholdRecipeContext context);
}
