package com.gastromind.api.domain.models.ticket;

import java.math.BigDecimal;

/**
 * Línea extraída del ticket (antes de resolver {@link com.gastromind.api.domain.models.Product} en catálogo).
 *
 * @param quantityAmount cantidad numérica en la unidad indicada (p. ej. 450 con g, 2 con ud)
 * @param quantityUnit   g, kg, ml, l o ud (unidades)
 * @param lineNeedsVerification true si la IA detecta datos dudosos (peso ilegible, cantidad ambigua, etc.)
 * @param lineQualityNote       detalle para el usuario; puede ser null
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
