package com.gastromind.api.domain.models;

import java.math.BigDecimal;

/**
 * Inventario agregado por producto en el hogar (nevera) para contextualizar la IA y validar cantidades.
 */
public record RecipeStockLine(String productId, String productName, BigDecimal quantityAvailable) {
}
