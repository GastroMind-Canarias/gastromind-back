package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoreRestMapper {

    @Mapping(target = "id", ignore = true)
    Store toDomain(StoreRequest request);

    StoreResponse toResponse(Store domain);

    List<StoreResponse> toResponseList(List<Store> stores);
}
