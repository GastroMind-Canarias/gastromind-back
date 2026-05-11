package com.gastromind.api.infrastructure.adapters.in.soap.store;

import com.gastromind.api.application.services.StoreServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Store;
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
class StoreCatalogSoapEndpointTest {

    @Mock
    private StoreServiceImpl storeService;

    @InjectMocks
    private StoreCatalogSoapEndpoint endpoint;

    @Test
    void listStores_includesNameNorm() {
        Store s = new Store();
        s.setId("s1");
        s.setName("Mercadona");
        s.setNameNorm("mercadona");
        when(storeService.findAll()).thenReturn(List.of(s));
        assertEquals("mercadona", endpoint.listStores()[0].getNameNorm());
    }

    @Test
    void getStoreById_fault() {
        when(storeService.findById("?")).thenThrow(new NotFoundException("missing store"));
        assertThrows(SOAPFaultException.class, () -> endpoint.getStoreById("?"));
    }
}
