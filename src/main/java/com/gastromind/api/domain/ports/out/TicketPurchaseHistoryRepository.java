package com.gastromind.api.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketPurchaseHistoryRepository {

    /**
     * Líneas de ticket del hogar (incluye legado sin household_id en ticket).
     */
    List<TicketPurchaseHistoryLine> findLinesForHouseholdSince(String householdId, LocalDateTime since);
}
