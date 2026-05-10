package com.gastromind.api.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lecturas agregadas sobre tickets pasados para sugerir compras habituales por hogar.
 */
public interface TicketPurchaseHistoryRepository {

    List<TicketPurchaseHistoryLine> findLinesForHouseholdSince(String householdId, LocalDateTime since);
}
