package com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Vista compacta del producto de catálogo en una línea de nevera (evita arrastrar alérgenos u otros datos pesados de la ficha completa).
 */
@Schema(description = "Resumen del producto de catálogo asociado al ítem; ausente si el ítem es solo etiqueta libre.")
public record FridgeItemProductSummaryResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Leche entera 1 L")
        String name,
        @Schema(example = "cat-dairy-001")
        String categoryId,
        @Schema(example = "Lácteos")
        String categoryName,
        @Schema(example = "true")
        boolean isEssential,
        @Schema(description = "true si conviene revisar el nombre o la categoría en catálogo")
        boolean needsReview
) {
}
