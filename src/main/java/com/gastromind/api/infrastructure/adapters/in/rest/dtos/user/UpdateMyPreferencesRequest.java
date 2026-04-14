package com.gastromind.api.infrastructure.adapters.in.rest.dtos.user;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Preferencias editables del perfil autenticado")
public record UpdateMyPreferencesRequest(
        @NotNull
        @Schema(description = "Ids de alérgenos. Se reemplaza conjunto completo.")
        List<String> allergenIds,
        @NotNull
        @Schema(description = "Tipos de electrodoméstico del hogar. Se reemplaza conjunto completo.")
        List<Appliance> appliances
) {
}
