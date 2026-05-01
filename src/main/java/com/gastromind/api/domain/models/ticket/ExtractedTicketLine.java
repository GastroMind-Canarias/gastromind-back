package com.gastromind.api.domain.models.ticket;

import java.math.BigDecimal;

/**
 * LAnea detectada durante la extracciAn automAtica de tickets.
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
