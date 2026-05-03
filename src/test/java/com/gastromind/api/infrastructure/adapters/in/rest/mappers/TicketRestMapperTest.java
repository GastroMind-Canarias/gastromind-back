package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketItemRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TicketRestMapperTest {

    private final TicketRestMapper mapper = new TicketRestMapperImpl();

    @Test
    void mapTicketItemRequests_mapsCatalogProductWhenProductIdPresent() {
        TicketItemRequest req = new TicketItemRequest(
                "  p-123  ",
                null,
                BigDecimal.ONE,
                new BigDecimal("1.25"),
                null,
                "ok",
                null);

        List<TicketItem> mapped = mapper.mapTicketItemRequests(List.of(req));

        assertEquals(1, mapped.size());
        assertNotNull(mapped.get(0).getProduct());
        assertEquals("p-123", mapped.get(0).getProduct().getId());
        assertNull(mapped.get(0).getLineProductName());
    }

    @Test
    void mapTicketItemRequests_mapsLineProductNameWhenProductIdMissing() {
        TicketItemRequest req = new TicketItemRequest(
                " ",
                "  Leche fresca sin catalogo ",
                new BigDecimal("2"),
                new BigDecimal("0.99"),
                null,
                "pending_review",
                " nota ");

        List<TicketItem> mapped = mapper.mapTicketItemRequests(List.of(req));

        assertEquals(1, mapped.size());
        assertNull(mapped.get(0).getProduct());
        assertEquals("Leche fresca sin catalogo", mapped.get(0).getLineProductName());
        assertEquals("nota", mapped.get(0).getLineNote());
    }

}
