package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Respuesta detallada de la receta")
/**
 * Representa recipe response dentro del dominio de la aplicacion.
 */
public record RecipeResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Huevos a la estampida")
        String title,
        @Schema(example = "1 - Cortar las papas para posteriormente anadirles sal al gusto y freirlas...")
        String instructions,
        @Schema(example = "4", description = "Cantidad de raciones de la receta")
        int servings,
        @Schema(example = "60", description = "Tiempo de preparacion en minutos")
        int prep_time,
        @Schema(example = "HORNO", description = "Electrodomestico usado", allowableValues = {
                "HORNO", "MICROONDAS", "AIR_FRYER", "VITROCERAMICA", "ROBOT_COCINA", "BATIDORA",
                "OLLA_EXPRESS", "FREIDORA", "GRILL"})
        Appliance appliance_needed,
        @Schema(example = "Media")
        String difficulty,
        @Schema(example = "10-12-2025")
        LocalDate created_at,
        @Schema(description = "Productos del inventario empleados y cantidades (p. ej. sugerencia IA)")
        List<IngredientUsageResponse> ingredientsUsed
) {
}






