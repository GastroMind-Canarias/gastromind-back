package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.util.List;

/**
 * Contexto de cocina del hogar para una única petición de sugerencia de receta (sin persistir en SQL).
 *
 * @param availableStock inventario por producto (cantidades agregadas de la nevera); la IA no debe superarlas.
 */
public record HouseholdRecipeContext(
        String householdId,
        List<RecipeStockLine> availableStock,
        List<String> allergenNamesToAvoid,
        List<Appliance> availableAppliances,
        int servings
) {}
