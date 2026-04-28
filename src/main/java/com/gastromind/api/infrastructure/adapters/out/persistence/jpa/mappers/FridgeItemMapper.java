package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
/**
 * Define el contrato de fridge item.
 */
public interface FridgeItemMapper {

    @Mapping(target = "fridge.id", source = "fridgeId")
    FridgeItemEntity toEntity(FridgeItem domain);

    @Mapping(target = "fridgeId", source = "fridge.id")
    FridgeItem toDomain(FridgeItemEntity entity);

    List<FridgeItemEntity> toEntityList(List<FridgeItem> domainList);

    List<FridgeItem> toDomainList(List<FridgeItemEntity> entityList);
}






