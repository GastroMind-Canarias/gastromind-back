package com.gastromind.api.domain.models;

import java.math.BigDecimal;

/**
 * Par item/cantidad para un consumo parcial (incluido en operaciones por lote).
 * No es entidad; solo agrupa lo que hace falta para descontar stock.
 */
public record FridgeItemConsumeLine(String itemId, BigDecimal quantity) {
}
