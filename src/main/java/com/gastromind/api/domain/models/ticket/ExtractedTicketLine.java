package com.gastromind.api.domain.models.ticket;

import java.math.BigDecimal;

/**
 * Línea cruda devuelta por el modelo de visión antes de cruzarla con catálogo y unidades.
 */
public record ExtractedTicketLine(
        String productName,
        BigDecimal quantityAmount,
        String quantityUnit,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        boolean lineNeedsVerification,
        String lineQualityNote
) {
}
