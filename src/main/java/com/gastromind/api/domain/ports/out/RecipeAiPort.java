package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Recipe;

/**
 * Puerto de salida: generación de una receta vía proveedor de IA (Gemini, etc.).
 */
public interface RecipeAiPort {

    /**
     * Genera exactamente una receta acorde al contexto del hogar.
     */
    Recipe generateOneRecipe(HouseholdRecipeContext context);
}
