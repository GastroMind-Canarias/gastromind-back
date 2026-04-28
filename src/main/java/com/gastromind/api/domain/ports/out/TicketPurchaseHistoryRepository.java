package com.gastromind.api.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Define el contrato de persistencia o integracion para ticket purchase history.
 */
public interface TicketPurchaseHistoryRepository {

    List<TicketPurchaseHistoryLine> findLinesForHouseholdSince(String householdId, LocalDateTime since);
}
