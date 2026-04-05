package com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Lista de ids de alérgenos del catálogo")
public record AllergenIdListRequest(
        @NotNull @Schema(description = "UUIDs de filas en allergen (puede estar vacío en PUT para borrar todos)")
        List<String> allergenIds
) {
}
