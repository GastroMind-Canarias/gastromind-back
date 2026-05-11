package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.PendingStoreResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreAliasResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de store rest.
 */
public interface StoreRestMapper {

    @Mapping(target = "id", ignore = true)
    Store toDomain(StoreRequest request);

    @Mapping(target = "name_norm", source = "nameNorm")
    StoreResponse toResponse(Store domain);

    List<StoreResponse> toResponseList(List<Store> stores);

    @Mapping(target = "store_id", source = "storeId")
    StoreAliasResponse toAliasResponse(StoreAlias domain);

    List<StoreAliasResponse> toAliasResponseList(List<StoreAlias> aliases);

    @Mapping(target = "detected_name", source = "detectedName")
    @Mapping(target = "sightings_count", source = "sightingsCount")
    @Mapping(target = "resolved_store_id", source = "resolvedStoreId")
    @Mapping(target = "rejection_reason", source = "rejectionReason")
    @Mapping(target = "status", expression = "java(domain.getStatus() != null ? domain.getStatus().name() : null)")
    PendingStoreResponse toPendingResponse(PendingStore domain);

    List<PendingStoreResponse> toPendingResponseList(List<PendingStore> pendingStores);
}






