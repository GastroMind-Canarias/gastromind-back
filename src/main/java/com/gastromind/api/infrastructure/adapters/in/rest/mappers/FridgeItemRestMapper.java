package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.MyFridgeItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de fridge item rest.
 */
public interface FridgeItemRestMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "fridgeId", source = "fridgeId")
    @Mapping(target = "id", ignore = true) // El ID suele ser gestionado por la base de datos en la creaciAAn
    FridgeItem toDomain(FridgeItemRequest request);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "fridgeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    FridgeItem toDomain(MyFridgeItemRequest request);

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






