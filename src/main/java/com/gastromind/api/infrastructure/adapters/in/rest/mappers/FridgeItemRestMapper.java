package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.MyFridgeItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FridgeItemRestMapper {

    /**
     * Transforma la petición del cliente (DTO) al modelo de dominio.
     * Mapeamos los IDs planos del DTO a la estructura de objetos del dominio.
     */
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "fridgeId", source = "fridgeId")
    @Mapping(target = "id", ignore = true) // El ID suele ser gestionado por la base de datos en la creación
    FridgeItem toDomain(FridgeItemRequest request);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "fridgeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    FridgeItem toDomain(MyFridgeItemRequest request);

    /**
     * Transforma el modelo de dominio a una respuesta legible para el cliente (DTO).
     * Extraemos el nombre del producto para facilitar la visualización en el frontend.
     */
    @Mapping(target = "productName", expression = "java(resolveFridgeProductName(domain))")
    FridgeItemResponse toResponse(FridgeItem domain);

    default String resolveFridgeProductName(FridgeItem domain) {
        if (domain.getProduct() != null && domain.getProduct().getName() != null) {
            return domain.getProduct().getName();
        }
        return domain.getProductLabel() != null ? domain.getProductLabel() : "";
    }


    List<FridgeItemResponse> toResponseList(List<FridgeItem> domainList);
}