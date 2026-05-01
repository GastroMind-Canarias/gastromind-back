package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de store.
 */
public interface StoreMapper {

    StoreEntity toEntity(Store domain);

    Store toDomain(StoreEntity entity);

    List<StoreEntity> toEntityList(List<Store> domainList);
    List<Store> toDomainList(List<StoreEntity> entityList);
}






