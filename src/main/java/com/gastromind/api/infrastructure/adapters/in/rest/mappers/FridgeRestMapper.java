package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FridgeRestMapper {
    @Mapping(target = "houseHold_id.id", source = "household_id")
    @Mapping(target = "id", ignore = true)
    Fridge toDomain(FridgeRequest request);

    @Mapping(target = "household_id", source = "houseHold_id.id")
    FridgeResponse toResponse(Fridge domain);

    List<FridgeResponse> toResponseList(List<Fridge> fridges);
}


