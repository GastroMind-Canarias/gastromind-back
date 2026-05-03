package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketProductResolutionServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TicketProductResolutionService service;

    @Test
    void findCatalogProductByName_returnsExistingWhenNameMatches() {
        Product p = new Product("p1", "Leche", false, null);
        when(productRepository.findFirstByNameIgnoreCase("leche")).thenReturn(Optional.of(p));
        assertEquals(Optional.of(p), service.findCatalogProductByName("  leche  "));
    }

    @Test
    void findCatalogProductByName_emptyWhenMissing() {
        when(productRepository.findFirstByNameIgnoreCase("Nuevo")).thenReturn(Optional.empty());
        assertTrue(service.findCatalogProductByName("Nuevo").isEmpty());
    }

    @Test
    void findCatalogProductByName_throwsWhenNameBlank() {
        assertThrows(IllegalArgumentException.class, () -> service.findCatalogProductByName("   "));
    }

    @Test
    void normalizeName_trimsAndCollapsesSpaces() {
        assertEquals("a b", TicketProductResolutionService.normalizeName("  a   b  "));
        assertEquals("", TicketProductResolutionService.normalizeName(null));
    }
}
