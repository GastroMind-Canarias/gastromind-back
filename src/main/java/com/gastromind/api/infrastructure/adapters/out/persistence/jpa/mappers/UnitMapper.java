package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    UnitEntity toEntity(Unit domain);

    Unit toDomain(UnitEntity entity);

    List<UnitEntity> toEntityList(List<Unit> domainList);
    List<Unit> toDomainList(List<UnitEntity> entityList);
}