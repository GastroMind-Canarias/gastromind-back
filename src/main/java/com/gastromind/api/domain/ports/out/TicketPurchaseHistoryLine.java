package com.gastromind.api.domain.ports.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Define el contrato de persistencia o integracion para ticket purchase history line.
 */
public record TicketPurchaseHistoryLine(
        String productId,
        String productName,
        String ticketId,
        LocalDateTime purchaseDate,
        BigDecimal quantityRaw,
        String unitNameFromDb
) {}
