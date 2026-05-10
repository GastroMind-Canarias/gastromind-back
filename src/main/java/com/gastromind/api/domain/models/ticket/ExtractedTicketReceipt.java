package com.gastromind.api.domain.models.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Cabecera y líneas tal como las interpretó la IA a partir de la foto del ticket.
 */
public record ExtractedTicketReceipt(
        String storeName,
        LocalDate purchaseDate,
        BigDecimal totalAmount,
        List<ExtractedTicketLine> lines
) {
}
