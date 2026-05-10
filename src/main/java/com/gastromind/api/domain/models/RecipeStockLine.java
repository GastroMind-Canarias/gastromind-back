package com.gastromind.api.domain.models;

import java.math.BigDecimal;

/**
 * Resumen de disponibilidad de un producto en la nevera del hogar al evaluar una receta.
 */
public record RecipeStockLine(String productId, String productName, BigDecimal quantityAvailable) {
}
