package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.ports.out.TicketPurchaseHistoryLine;
import com.gastromind.api.domain.ports.out.TicketPurchaseHistoryRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketItemEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.TicketItemJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TicketPurchaseHistoryAdapter implements TicketPurchaseHistoryRepository {

    private final TicketItemJpaRepository ticketItemJpaRepository;

    public TicketPurchaseHistoryAdapter(TicketItemJpaRepository ticketItemJpaRepository) {
        this.ticketItemJpaRepository = ticketItemJpaRepository;
    }

    @Override
    public List<TicketPurchaseHistoryLine> findLinesForHouseholdSince(String householdId, LocalDateTime since) {
        if (householdId == null || householdId.isBlank()) {
            return List.of();
        }
        List<TicketItemEntity> rows = ticketItemJpaRepository.findForHouseholdSince(householdId, since);
        return rows.stream()
                .map(ti -> new TicketPurchaseHistoryLine(
                        ti.getProduct().getId(),
                        ti.getProduct().getName(),
                        ti.getTicket().getId(),
                        ti.getTicket().getPurchaseDate(),
                        ti.getQuantity(),
                        ti.getUnit().getName()))
                .toList();
    }
}
