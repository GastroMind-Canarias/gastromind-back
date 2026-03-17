package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Información del ticket devuelta por la API")
public record TicketResponse(
        @Schema(example = "tk-998877", description = "ID único del ticket")
        String id,

        @Schema(example = "user-123-abc")
        String user_id,

        @Schema(example = "store-mercadona-01")
        String store_id,

        @Schema(example = "45.25")
        float total_amount,

        @Schema(example = "2024-03-15")
        LocalDate purchaseDate
) {}
