package com.gastromind.api.infrastructure.adapters.in.soap.category;

import com.gastromind.api.application.services.CategoryServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapCatalogFaults;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.CategorySoapDto;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa SOAP fina: no mete reglas nuevas, solo traduce a XML lo que ya responde {@link CategoryServiceImpl}.
 * Está colgando sin JWT porque la asignación prioriza demostrar el estándar; en un despliegue real habría que blindarlo.
 */
@Service
@WebService(
        serviceName = "CategoryCatalogService",
        portName = "CategoryCatalogPort",
        name = "CategoryCatalog",
        endpointInterface = "com.gastromind.api.infrastructure.adapters.in.soap.category.CategoryCatalogSoapApi",
        targetNamespace = SoapNamespaces.CATALOG
)
public class CategoryCatalogSoapEndpoint implements CategoryCatalogSoapApi {

    private final CategoryServiceImpl categoryService;

    public CategoryCatalogSoapEndpoint(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategorySoapDto[] listCategories() {
        List<Category> all = categoryService.findAll();
        CategorySoapDto[] out = new CategorySoapDto[all.size()];
        for (int i = 0; i < all.size(); i++) {
            out[i] = toDto(all.get(i));
        }
        return out;
    }

    @Override
    public CategorySoapDto getCategoryById(String id) {
        try {
            return toDto(categoryService.findById(id));
        } catch (NotFoundException e) {
            throw SoapCatalogFaults.notFound(e.getMessage());
        }
    }

    private static CategorySoapDto toDto(Category c) {
        return new CategorySoapDto(c.getId(), c.getName());
    }
}
