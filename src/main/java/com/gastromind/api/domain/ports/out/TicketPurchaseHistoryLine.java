package com.gastromind.api.domain.ports.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Línea de ticket para agregar historial de compras (hogar: varios usuarios).
 */
public record TicketPurchaseHistoryLine(
        String productId,
        String productName,
        String ticketId,
        LocalDateTime purchaseDate,
        BigDecimal quantityRaw,
        String unitNameFromDb
) {}
