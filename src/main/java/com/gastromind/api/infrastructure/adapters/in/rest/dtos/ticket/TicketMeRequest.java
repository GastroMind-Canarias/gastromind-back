package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Registrar un ticket para el usuario autenticado (sin user_id en cuerpo)")
/**
 * Representa ticket me request dentro del dominio de la aplicacion.
 */
public record TicketMeRequest(

        @Schema(example = "store-mercadona-01", description = "ID del establecimiento")
        @NotBlank(message = "El ID del establecimiento es obligatorio")
        String store_id,

        @Schema(example = "45.25", description = "Importe total de la compra")
        @NotNull(message = "El importe total no puede ser nulo")
        @Positive(message = "El importe debe ser mayor que cero")
        float total_mount,

        @Schema(example = "2024-03-15", description = "Fecha en la que se realizo la compra")
        @NotNull(message = "La fecha de compra es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate purchaseDate,

        @Schema(description = "Lineas del ticket (opcional; vacio = solo cabecera)")
        List<@Valid TicketItemRequest> items
) {}






