package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreAliasEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Convierte alias de tienda entre modelo de dominio y filas JPA con {@code StoreEntity}.
 */
@Mapper(componentModel = "spring")
public interface StoreAliasMapper {
    @Mapping(target = "store.id", source = "storeId")
    StoreAliasEntity toEntity(StoreAlias domain);

    @Mapping(target = "storeId", source = "store.id")
    StoreAlias toDomain(StoreAliasEntity entity);

    List<StoreAlias> toDomainList(List<StoreAliasEntity> entities);
}
