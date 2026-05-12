package com.gastromind.api.infrastructure.adapters.in.soap.product;

import com.gastromind.api.application.services.ProductServiceImpl;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapCatalogFaults;
import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.ProductSoapDto;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Mapea productos a DTOs aplanados: evita referencias circulares JAXB y mantiene la lectura simple para corrección manual.
 */
@Service
@WebService(
        serviceName = "ProductCatalogService",
        portName = "ProductCatalogPort",
        name = "ProductCatalog",
        endpointInterface = "com.gastromind.api.infrastructure.adapters.in.soap.product.ProductCatalogSoapApi",
        targetNamespace = SoapNamespaces.CATALOG
)
public class ProductCatalogSoapEndpoint implements ProductCatalogSoapApi {

    private final ProductServiceImpl productService;

    public ProductCatalogSoapEndpoint(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Override
    public ProductSoapDto[] listProducts() {
        List<Product> all = productService.findAll();
        ProductSoapDto[] out = new ProductSoapDto[all.size()];
        for (int i = 0; i < all.size(); i++) {
            out[i] = toDto(all.get(i));
        }
        return out;
    }

    @Override
    public ProductSoapDto getProductById(String id) {
        try {
            return toDto(productService.findById(id));
        } catch (NotFoundException e) {
            throw SoapCatalogFaults.notFound(e.getMessage());
        }
    }

    private static ProductSoapDto toDto(Product p) {
        ProductSoapDto d = new ProductSoapDto();
        d.setId(p.getId());
        d.setName(p.getName());
        d.setEssential(p.isIs_essential());
        d.setNeedsReview(p.isNeedsReview());
        d.setReviewNote(p.getReviewNote());
        Allergen a = p.getAllergen();
        if (a != null) {
            d.setAllergenId(a.getId());
            d.setAllergenName(a.getName());
        }
        Category c = p.getCategory();
        if (c != null) {
            d.setCategoryId(c.getId());
            d.setCategoryName(c.getName());
        }
        return d;
    }
}
