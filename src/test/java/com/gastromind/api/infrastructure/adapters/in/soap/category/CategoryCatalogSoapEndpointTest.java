package com.gastromind.api.infrastructure.adapters.in.soap.category;

import com.gastromind.api.application.services.CategoryServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryCatalogSoapEndpointTest {

    @Mock
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private CategoryCatalogSoapEndpoint endpoint;

    @Test
    void listCategories_mapsAll() {
        when(categoryService.findAll()).thenReturn(List.of(new Category("1", "A"), new Category("2", "B")));
        var arr = endpoint.listCategories();
        assertEquals(2, arr.length);
        assertEquals("A", arr[0].getName());
        assertEquals("2", arr[1].getId());
    }

    @Test
    void getCategoryById_found() {
        when(categoryService.findById("x")).thenReturn(new Category("x", "Z"));
        assertEquals("Z", endpoint.getCategoryById("x").getName());
    }

    @Test
    void getCategoryById_notFound_fault() {
        when(categoryService.findById("nope")).thenThrow(new NotFoundException("gone"));
        assertThrows(SOAPFaultException.class, () -> endpoint.getCategoryById("nope"));
    }
}
