package com.gastromind.api.infrastructure.adapters.in.soap.unit;

import com.gastromind.api.application.services.UnitServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapCatalogFaults;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.UnitSoapDto;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Expone kg, litros, etc. por SOAP sin tocar la lógica de negocio; útil para la rúbrica, no tanto para el front móvil.
 */
@Service
@WebService(
        serviceName = "UnitCatalogService",
        portName = "UnitCatalogPort",
        name = "UnitCatalog",
        endpointInterface = "com.gastromind.api.infrastructure.adapters.in.soap.unit.UnitCatalogSoapApi",
        targetNamespace = SoapNamespaces.CATALOG
)
public class UnitCatalogSoapEndpoint implements UnitCatalogSoapApi {

    private final UnitServiceImpl unitService;

    public UnitCatalogSoapEndpoint(UnitServiceImpl unitService) {
        this.unitService = unitService;
    }

    @Override
    public UnitSoapDto[] listUnits() {
        List<Unit> all = unitService.findAll();
        UnitSoapDto[] out = new UnitSoapDto[all.size()];
        for (int i = 0; i < all.size(); i++) {
            out[i] = toDto(all.get(i));
        }
        return out;
    }

    @Override
    public UnitSoapDto getUnitById(String id) {
        try {
            return toDto(unitService.findById(id));
        } catch (NotFoundException e) {
            throw SoapCatalogFaults.notFound(e.getMessage());
        }
    }

    private static UnitSoapDto toDto(Unit u) {
        return new UnitSoapDto(u.getId(), u.getName());
    }
}
