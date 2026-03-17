package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreEntity;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    StoreEntity toEntity(Store domain);

    Store toDomain(StoreEntity entity);

    List<StoreEntity> toEntityList(List<Store> domainList);
    List<Store> toDomainList(List<StoreEntity> entityList);
}