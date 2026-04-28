package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.util.List;

/**
 * Contexto de dominio usado para sugerir recetas en un hogar.
 */
public record HouseholdRecipeContext(
        String householdId,
        List<RecipeStockLine> availableStock,
        List<String> allergenNamesToAvoid,
        List<Appliance> availableAppliances,
        int servings
) {}
