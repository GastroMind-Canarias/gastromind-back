package com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Informacion del ticket devuelta por la API")
/**
 * Representa ticket response dentro del dominio de la aplicacion.
 */
public record TicketResponse(
        @Schema(example = "tk-998877", description = "ID Unico del ticket")
        String id,

        @Schema(description = "Hogar al que esta asociado el ticket")
        String household_id,

        @Schema(description = "Usuario que registro o importo el ticket")
        String uploaded_by_user_id,

        @Schema(description = "Igual que uploaded_by_user_id (compatibilidad con clientes antiguos)")
        String user_id,

        @Schema(example = "store-mercadona-01")
        String store_id,

        @Schema(example = "45.25")
        float total_amount,

        @Schema(example = "2024-03-15")
        LocalDate purchaseDate,

        @Schema(description = "Lineas del ticket con productos del catalogo")
        List<TicketItemResponse> items,
        @Schema(description = "Informacion de tienda pendiente cuando no se pudo resolver")
        PendingStoreInfoResponse pending_store
) {}






