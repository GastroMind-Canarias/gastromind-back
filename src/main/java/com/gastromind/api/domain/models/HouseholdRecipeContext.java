package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.util.List;

/**
 * Snapshot enviado al motor de recetas: stock disponible, alérgenos a evitar, electrodomésticos y raciones deseadas.
 */
public record HouseholdRecipeContext(
        String householdId,
        List<RecipeStockLine> availableStock,
        List<String> allergenNamesToAvoid,
        List<Appliance> availableAppliances,
        int servings
) {}
