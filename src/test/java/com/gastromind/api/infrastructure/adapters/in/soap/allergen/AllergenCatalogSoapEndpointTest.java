package com.gastromind.api.infrastructure.adapters.in.soap.allergen;

import com.gastromind.api.application.services.AllergenServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
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
class AllergenCatalogSoapEndpointTest {

    @Mock
    private AllergenServiceImpl allergenService;

    @InjectMocks
    private AllergenCatalogSoapEndpoint endpoint;

    @Test
    void listAllergens_maps() {
        when(allergenService.findAll()).thenReturn(List.of(new Allergen("a", "Gluten")));
        assertEquals("Gluten", endpoint.listAllergens()[0].getName());
    }

    @Test
    void getAllergenById_fault() {
        when(allergenService.findById("q")).thenThrow(new NotFoundException("x"));
        assertThrows(SOAPFaultException.class, () -> endpoint.getAllergenById("q"));
    }
}
