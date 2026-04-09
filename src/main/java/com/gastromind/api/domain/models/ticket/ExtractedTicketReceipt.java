package com.gastromind.api.domain.models.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExtractedTicketReceipt(
        String storeName,
        LocalDate purchaseDate,
        BigDecimal totalAmount,
        List<ExtractedTicketLine> lines
) {
}
