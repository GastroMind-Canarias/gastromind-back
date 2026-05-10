package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemProductSummaryResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.MyFridgeItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
/**
 * Pasa de dominio a DTO de API; el nombre mostrable sigue viniendo del catálogo o de la etiqueta libre, y el bloque {@code product} resume el catálogo cuando existe id de producto.
 */
public interface FridgeItemRestMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "fridgeId", source = "fridgeId")
    @Mapping(target = "id", ignore = true) // El ID suele ser gestionado por la base de datos en la creacion
    FridgeItem toDomain(FridgeItemRequest request);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "fridgeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    FridgeItem toDomain(MyFridgeItemRequest request);

    @Mapping(target = "productName", expression = "java(resolveFridgeProductName(domain))")
    @Mapping(target = "product", expression = "java(toProductSummary(domain))")
    FridgeItemResponse toResponse(FridgeItem domain);

    /**
     * Arma el resumen cuando hay producto de catálogo; si no hay id de producto devuelve null (ítem solo etiqueta).
     */
    default FridgeItemProductSummaryResponse toProductSummary(FridgeItem domain) {
        Product p = domain.getProduct();
        if (p == null || p.getId() == null) {
            return null;
        }
        Category c = p.getCategory();
        String categoryId = c != null ? c.getId() : null;
        String categoryName = c != null ? c.getName() : null;
        return new FridgeItemProductSummaryResponse(
                p.getId(),
                p.getName(),
                categoryId,
                categoryName,
                p.isIs_essential(),
                p.isNeedsReview());
    }

    default String resolveFridgeProductName(FridgeItem domain) {
        if (domain.getProduct() != null && domain.getProduct().getName() != null) {
            return domain.getProduct().getName();
        }
        return domain.getProductLabel() != null ? domain.getProductLabel() : "";
    }


    List<FridgeItemResponse> toResponseList(List<FridgeItem> domainList);
}






