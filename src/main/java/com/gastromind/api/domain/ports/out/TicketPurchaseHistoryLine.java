package com.gastromind.api.domain.ports.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vista compacta de una línea histórica de ticket para sugerir compras habituales.
 */
public record TicketPurchaseHistoryLine(
        String productId,
        String productName,
        String ticketId,
        LocalDateTime purchaseDate,
        BigDecimal quantityRaw,
        String unitNameFromDb
) {}
