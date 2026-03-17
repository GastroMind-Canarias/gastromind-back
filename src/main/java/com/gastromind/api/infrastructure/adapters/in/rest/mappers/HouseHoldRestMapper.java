package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HouseHoldRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    HouseHold toDomain(HouseHoldRequest request);

    HouseHoldResponse toResponse(HouseHold domain);

    List<HouseHoldResponse> toResponseList(List<HouseHold> houseHolds);
}
