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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketProductResolutionServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TicketProductResolutionService service;

    @Test
    void resolveOrCreate_returnsExistingWhenNameMatches() {
        Product p = new Product("p1", "Leche", false, null);
        when(productRepository.findFirstByNameIgnoreCase("leche")).thenReturn(Optional.of(p));
        assertEquals(p, service.resolveOrCreate("  leche  "));
    }

    @Test
    void resolveOrCreate_createsWhenMissing() {
        when(productRepository.findFirstByNameIgnoreCase("Nuevo")).thenReturn(Optional.empty());
        Product saved = new Product("n1", "Nuevo", false, null);
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        Product result = service.resolveOrCreate("Nuevo");
        assertEquals("Nuevo", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void resolveOrCreate_throwsWhenNameBlank() {
        assertThrows(IllegalArgumentException.class, () -> service.resolveOrCreate("   "));
    }

    @Test
    void normalizeName_trimsAndCollapsesSpaces() {
        assertEquals("a b", TicketProductResolutionService.normalizeName("  a   b  "));
        assertEquals("", TicketProductResolutionService.normalizeName(null));
    }
}
