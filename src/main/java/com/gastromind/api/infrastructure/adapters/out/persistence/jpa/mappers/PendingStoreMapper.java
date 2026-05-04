package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.PendingStoreEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PendingStoreMapper {
    PendingStoreEntity toEntity(PendingStore domain);

    PendingStore toDomain(PendingStoreEntity entity);

    List<PendingStore> toDomainList(List<PendingStoreEntity> entities);
}
