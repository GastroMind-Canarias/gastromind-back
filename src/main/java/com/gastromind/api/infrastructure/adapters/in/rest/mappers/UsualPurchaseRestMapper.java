package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsualPurchaseRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user_id.id", source = "user_id")
    @Mapping(target = "product_id.id", source = "product_id")
    UsualPurchase toDomain(UsualPurchaseRequest request);

    @Mapping(target = "user_id", source = "user_id.id")
    @Mapping(target = "product_id", source = "product_id.id")
    UsualPurchaseResponse toResponse(UsualPurchase domain);

    List<UsualPurchaseResponse> toResponseList(List<UsualPurchase> usualPurchases);
}
