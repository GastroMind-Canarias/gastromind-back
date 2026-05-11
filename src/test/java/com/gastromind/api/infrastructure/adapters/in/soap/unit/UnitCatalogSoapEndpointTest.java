package com.gastromind.api.infrastructure.adapters.in.soap.unit;

import com.gastromind.api.application.services.UnitServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Unit;
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
class UnitCatalogSoapEndpointTest {

    @Mock
    private UnitServiceImpl unitService;

    @InjectMocks
    private UnitCatalogSoapEndpoint endpoint;

    @Test
    void listUnits_empty() {
        when(unitService.findAll()).thenReturn(List.of());
        assertEquals(0, endpoint.listUnits().length);
    }

    @Test
    void getUnitById_fault() {
        when(unitService.findById("z")).thenThrow(new NotFoundException("no"));
        assertThrows(SOAPFaultException.class, () -> endpoint.getUnitById("z"));
    }
}
