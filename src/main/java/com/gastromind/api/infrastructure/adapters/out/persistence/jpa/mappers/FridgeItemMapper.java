package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface FridgeItemMapper {

    @Mapping(target = "fridge.id", source = "fridgeId")
    FridgeItemEntity toEntity(FridgeItem domain);

    @Mapping(target = "fridgeId", source = "fridge.id")
    FridgeItem toDomain(FridgeItemEntity entity);

    List<FridgeItemEntity> toEntityList(List<FridgeItem> domainList);

    List<FridgeItem> toDomainList(List<FridgeItemEntity> entityList);
}