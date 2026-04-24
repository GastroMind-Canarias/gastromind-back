package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketItemEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.TicketItemJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketPurchaseHistoryAdapterTest {

    @Mock
    private TicketItemJpaRepository ticketItemJpaRepository;

    @InjectMocks
    private TicketPurchaseHistoryAdapter adapter;

    @Test
    void blankHousehold_returnsEmpty() {
        LocalDateTime since = LocalDateTime.now();
        assertTrue(adapter.findLinesForHouseholdSince(null, since).isEmpty());
        assertTrue(adapter.findLinesForHouseholdSince("", since).isEmpty());
        assertTrue(adapter.findLinesForHouseholdSince("   ", since).isEmpty());
    }

    @Test
    void mapsTicketLines() {
        LocalDateTime since = LocalDateTime.now().minusDays(1);
        LocalDateTime purchase = LocalDateTime.now();

        ProductEntity product = new ProductEntity();
        product.setId("p-1");
        product.setName("Leche");
        TicketEntity ticket = new TicketEntity();
        ticket.setId("t-1");
        ticket.setPurchaseDate(purchase);
        UnitEntity unit = new UnitEntity();
        unit.setName("ud");

        TicketItemEntity row = new TicketItemEntity();
        row.setProduct(product);
        row.setTicket(ticket);
        row.setUnit(unit);
        row.setQuantity(new BigDecimal("2"));

        when(ticketItemJpaRepository.findForHouseholdSince("h-1", since)).thenReturn(List.of(row));

        var lines = adapter.findLinesForHouseholdSince("h-1", since);
        assertEquals(1, lines.size());
        assertEquals("p-1", lines.getFirst().productId());
        assertEquals("Leche", lines.getFirst().productName());
        assertEquals("t-1", lines.getFirst().ticketId());
        assertEquals(purchase, lines.getFirst().purchaseDate());
        assertEquals(new BigDecimal("2"), lines.getFirst().quantityRaw());
        assertEquals("ud", lines.getFirst().unitNameFromDb());
    }
}
