package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeEntity;

@Mapper(componentModel = "spring", uses = {HouseholdMapper.class})
public interface FridgeMapper {

    @Mapping(source = "houseHold_id", target = "household")
    FridgeEntity toEntity(Fridge domain);

    @Mapping(source = "household", target = "houseHold_id")
    Fridge toDomain(FridgeEntity entity);

    List<FridgeEntity> toEntityList(List<Fridge> domainList);
    List<Fridge> toDomainList(List<FridgeEntity> entityList);
}