package com.gastromind.api.infrastructure.adapters.in.soap.store;

import com.gastromind.api.application.services.StoreServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapCatalogFaults;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.StoreSoapDto;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Expone tiendas ya resueltas en base de datos; no crea alias ni gestiona pendientes (eso sigue en REST).
 */
@Service
@WebService(
        serviceName = "StoreCatalogService",
        portName = "StoreCatalogPort",
        name = "StoreCatalog",
        endpointInterface = "com.gastromind.api.infrastructure.adapters.in.soap.store.StoreCatalogSoapApi",
        targetNamespace = SoapNamespaces.CATALOG
)
public class StoreCatalogSoapEndpoint implements StoreCatalogSoapApi {

    private final StoreServiceImpl storeService;

    public StoreCatalogSoapEndpoint(StoreServiceImpl storeService) {
        this.storeService = storeService;
    }

    @Override
    public StoreSoapDto[] listStores() {
        List<Store> all = storeService.findAll();
        StoreSoapDto[] out = new StoreSoapDto[all.size()];
        for (int i = 0; i < all.size(); i++) {
            out[i] = toDto(all.get(i));
        }
        return out;
    }

    @Override
    public StoreSoapDto getStoreById(String id) {
        try {
            return toDto(storeService.findById(id));
        } catch (NotFoundException e) {
            throw SoapCatalogFaults.notFound(e.getMessage());
        }
    }

    private static StoreSoapDto toDto(Store s) {
        StoreSoapDto d = new StoreSoapDto();
        d.setId(s.getId());
        d.setName(s.getName());
        d.setNameNorm(s.getNameNorm());
        return d;
    }
}
