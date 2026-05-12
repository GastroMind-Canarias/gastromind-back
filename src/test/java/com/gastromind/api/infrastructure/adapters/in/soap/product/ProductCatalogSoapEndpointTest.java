package com.gastromind.api.infrastructure.adapters.in.soap.product;

import com.gastromind.api.application.services.ProductServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.models.Product;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogSoapEndpointTest {

    @Mock
    private ProductServiceImpl productService;

    @InjectMocks
    private ProductCatalogSoapEndpoint endpoint;

    @Test
    void listProducts_flattensRelations() {
        Product p = new Product();
        p.setId("p1");
        p.setName("Leche");
        p.setIs_essential(true);
        p.setNeedsReview(false);
        p.setReviewNote(null);
        p.setAllergen(new Allergen("al1", "Leche"));
        p.setCategory(new Category("c1", "Bebidas"));
        when(productService.findAll()).thenReturn(List.of(p));
        var dto = endpoint.listProducts()[0];
        assertEquals("p1", dto.getId());
        assertTrue(dto.isEssential());
        assertEquals("al1", dto.getAllergenId());
        assertEquals("c1", dto.getCategoryId());
    }

    @Test
    void getProductById_fault() {
        when(productService.findById("bad")).thenThrow(new NotFoundException("np"));
        assertThrows(SOAPFaultException.class, () -> endpoint.getProductById("bad"));
    }
}
