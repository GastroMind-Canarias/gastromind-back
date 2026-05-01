package com.gastromind.api.infrastructure.adapters.in.rest.dtos.user;

import com.gastromind.api.domain.models.enums.Appliance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Preferencias editables del perfil autenticado")
/**
 * Representa update my preferences request dentro del dominio de la aplicacion.
 */
public record UpdateMyPreferencesRequest(
        @NotNull
        @Schema(description = "Ids de alAArgenos. Se reemplaza conjunto completo.")
        List<String> allergenIds,
        @NotNull
        @Schema(description = "Tipos de electrodomAAstico del hogar. Se reemplaza conjunto completo.")
        List<Appliance> appliances
) {
}






