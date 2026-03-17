package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos necesarios para registrar un ticket")
public record TicketRequest(

        @Schema(example = "user-123-abc", description = "ID del usuario que sube el ticket")
        @NotBlank(message = "El ID de usuario es obligatorio")
        String user_id,

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
        LocalDate purchaseDate
) {}
