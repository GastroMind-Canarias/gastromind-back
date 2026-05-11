package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.ProductAlias;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductAliasEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convierte alias de producto desnormalizando la FK a identificador de producto.
 */
@Mapper(componentModel = "spring")
public interface ProductAliasMapper {
    @Mapping(target = "product.id", source = "productId")
    ProductAliasEntity toEntity(ProductAlias domain);

    @Mapping(target = "productId", source = "product.id")
    ProductAlias toDomain(ProductAliasEntity entity);
}
