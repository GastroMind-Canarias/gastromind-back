package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.ProductAlias;
import com.gastromind.api.domain.ports.out.ProductAliasRepository;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketProductResolutionServiceTest {

    @Test
    void findCatalogProductByName_returnsExistingWhenNameMatches() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductAliasRepository aliasRepository = mock(ProductAliasRepository.class);
        TicketProductResolutionService service = new TicketProductResolutionService(productRepository, aliasRepository);
        Product p = new Product("p1", "Leche", false, null);
        when(productRepository.findFirstByNameIgnoreCase("leche")).thenReturn(Optional.of(p));
        assertEquals(Optional.of(p), service.findCatalogProductByName("  leche  "));
    }

    @Test
    void findCatalogProductByName_throwsWhenNameBlank() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductAliasRepository aliasRepository = mock(ProductAliasRepository.class);
        TicketProductResolutionService service = new TicketProductResolutionService(productRepository, aliasRepository);
        assertThrows(IllegalArgumentException.class, () -> service.findCatalogProductByName("   "));
    }

    @Test
    void resolveOrCreateProduct_returnsExactCatalogMatch() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductAliasRepository aliasRepository = mock(ProductAliasRepository.class);
        TicketProductResolutionService service = new TicketProductResolutionService(productRepository, aliasRepository);
        Product existing = new Product("prod-1");
        when(productRepository.findFirstByNameIgnoreCase("tomate")).thenReturn(Optional.of(existing));
        Product resolved = service.resolveOrCreateProduct("Tomate");
        assertEquals("prod-1", resolved.getId());
        verify(productRepository, never()).save(any());
    }

    @Test
    void resolveOrCreateProduct_usesAliasWhenCatalogNameDiffers() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductAliasRepository aliasRepository = mock(ProductAliasRepository.class);
        TicketProductResolutionService service = new TicketProductResolutionService(productRepository, aliasRepository);
        Product existing = new Product("prod-2");
        ProductAlias alias = new ProductAlias();
        alias.setProductId("prod-2");
        when(productRepository.findFirstByNameIgnoreCase("tomate pera extra")).thenReturn(Optional.empty());
        when(aliasRepository.findFirstByAliasNorm("tomate pera extra")).thenReturn(Optional.of(alias));
        when(productRepository.findById("prod-2")).thenReturn(Optional.of(existing));
        Product resolved = service.resolveOrCreateProduct("tomate pera extra");
        assertEquals("prod-2", resolved.getId());
        verify(productRepository, never()).save(any());
    }

    @Test
    void resolveOrCreateProduct_createsProvisionalWhenNoMatch() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductAliasRepository aliasRepository = mock(ProductAliasRepository.class);
        TicketProductResolutionService service = new TicketProductResolutionService(productRepository, aliasRepository);
        when(productRepository.findFirstByNameIgnoreCase("producto nuevo")).thenReturn(Optional.empty());
        when(aliasRepository.findFirstByAliasNorm("producto nuevo")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId("prod-new");
            return p;
        });
        Product resolved = service.resolveOrCreateProduct("Producto   Nuevo");
        assertEquals("prod-new", resolved.getId());
        assertTrue(resolved.isNeedsReview());
        verify(aliasRepository).save(any(ProductAlias.class));
    }
}
