package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "Respuesta detallada de la receta")
public record RecipeResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        @Schema(example = "Huevos a la estampida")
        String title,
        @Schema(example = "1º - Cortar las papas para posteriormente añadirles sal al gusto y freirlas...")
        String instructions,
        @Schema(example = "4", description = "Cantidad de raciones de la receta")
        int servings,
        @Schema(example = "60", description = "Tiempo de preparacion en minutos")
        int prep_time,
        @Schema(example = "HORNO", description = "Electrodomestico usado", allowableValues = {"HORNO", "MICROONDAS", "AIR_FRYER", "VITROCERAMICA", "ROBOT_COCINA", "BATIDORA"})
        Appliance appliance_needed,
        @Schema(example = "Media")
        String difficulty,
        @Schema(example = "10-12-2025")
        LocalDate created_at
) {
}
