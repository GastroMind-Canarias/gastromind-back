package com.gastromind.api.domain.models;

import java.math.BigDecimal;

/**
 * Modelo de dominio para una línea de stock en receta.
 */
public record RecipeStockLine(String productId, String productName, BigDecimal quantityAvailable) {
}
