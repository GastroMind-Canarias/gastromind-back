package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitRestMapper {

    @Mapping(target = "id", ignore = true)
    Unit toDomain(UnitRequest request);

    UnitResponse toResponse(Unit domain);

    List<UnitResponse> toResponseList(List<Unit> units);
}
