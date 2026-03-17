package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "allergen.id", source = "allergen_id")
    Product toDomain(ProductRequest request);

    @Mapping(target = "allergen_id", source = "allergen.id")
    ProductResponse toResponse(Product domain);

    List<ProductResponse> toResponseList(List<Product> products);
}
