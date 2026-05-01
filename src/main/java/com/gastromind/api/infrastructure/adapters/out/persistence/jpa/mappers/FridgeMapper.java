package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {HouseholdMapper.class})
/**
 * Define el contrato de fridge.
 */
public interface FridgeMapper {

    @Mapping(source = "houseHold_id", target = "household")
    FridgeEntity toEntity(Fridge domain);

    @Mapping(source = "household", target = "houseHold_id")
    Fridge toDomain(FridgeEntity entity);

    List<FridgeEntity> toEntityList(List<Fridge> domainList);
    List<Fridge> toDomainList(List<FridgeEntity> entityList);
}






