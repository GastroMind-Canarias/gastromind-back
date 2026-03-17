package com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "Objeto para registrar una nueva receta")
public record RecipeRequest(
        @Schema(example = "Huevos a la estampida")
        @NotBlank(message = "El nombre de la receta es obligatorio")
        String title,
        @Schema(example = "1º - Cortar las papas para posteriormente añadirles sal al gusto y freirlas...")
        @NotBlank(message = "Las instrucciones para realizar la receta son obligatorias")
        String instructions,
        @Schema(example = "4", description = "Cantidad de raciones de la receta")
        @NotNull(message = "Las raciones son obligatorias")
        @Positive(message = "La cantidad de raciones debe ser positivo")
        int servings,
        @Schema(example = "60", description = "Tiempo de preparacion en minutos")
        @NotNull(message = "El tiempo de preparacion es obligatorio")
        @Positive(message = "La cantidad de minutos debe ser positiva")
        int prep_time,
        @Schema(example = "HORNO", description = "Electrodomestico usado", allowableValues = {"HORNO", "MICROONDAS", "AIR_FRYER", "VITROCERAMICA", "ROBOT_COCINA", "BATIDORA"})
        Appliance appliance_needed,
        @Schema(example = "Media")
        @NotBlank(message = "La dificultad debe estar")
        String difficulty,
        @Schema(example = "2024-03-15", description = "Fecha en la que se realizo la receta")
        @NotNull(message = "La fecha de receta es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate created_at
) {
}
