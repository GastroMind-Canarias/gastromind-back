package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.ProductAlias;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductAliasEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAliasMapper {
    @Mapping(target = "product.id", source = "productId")
    ProductAliasEntity toEntity(ProductAlias domain);

    @Mapping(target = "productId", source = "product.id")
    ProductAlias toDomain(ProductAliasEntity entity);
}
