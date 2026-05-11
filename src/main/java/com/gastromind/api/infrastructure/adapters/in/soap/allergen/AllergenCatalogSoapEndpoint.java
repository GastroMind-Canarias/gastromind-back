package com.gastromind.api.infrastructure.adapters.in.soap.allergen;

import com.gastromind.api.application.services.AllergenServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapCatalogFaults;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.AllergenSoapDto;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sirve el listado de alérgenos para integraciones “legacy”; no sustituye al flujo principal con JSON.
 */
@Service
@WebService(
        serviceName = "AllergenCatalogService",
        portName = "AllergenCatalogPort",
        name = "AllergenCatalog",
        endpointInterface = "com.gastromind.api.infrastructure.adapters.in.soap.allergen.AllergenCatalogSoapApi",
        targetNamespace = SoapNamespaces.CATALOG
)
public class AllergenCatalogSoapEndpoint implements AllergenCatalogSoapApi {

    private final AllergenServiceImpl allergenService;

    public AllergenCatalogSoapEndpoint(AllergenServiceImpl allergenService) {
        this.allergenService = allergenService;
    }

    @Override
    public AllergenSoapDto[] listAllergens() {
        List<Allergen> all = allergenService.findAll();
        AllergenSoapDto[] out = new AllergenSoapDto[all.size()];
        for (int i = 0; i < all.size(); i++) {
            out[i] = toDto(all.get(i));
        }
        return out;
    }

    @Override
    public AllergenSoapDto getAllergenById(String id) {
        try {
            return toDto(allergenService.findById(id));
        } catch (NotFoundException e) {
            throw SoapCatalogFaults.notFound(e.getMessage());
        }
    }

    private static AllergenSoapDto toDto(Allergen a) {
        return new AllergenSoapDto(a.getId(), a.getName());
    }
}
