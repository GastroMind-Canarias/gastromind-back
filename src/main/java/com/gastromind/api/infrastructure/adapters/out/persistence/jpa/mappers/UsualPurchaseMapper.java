package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ProductMapper.class})
public interface UsualPurchaseMapper {

    @Mapping(source = "user_id", target = "user")
    @Mapping(source = "product_id", target = "product")
    @Mapping(source = "target_quantity", target = "targetQuantity")
    UsualPurchaseEntity toEntity(UsualPurchase domain);

    @Mapping(source = "user", target = "user_id")
    @Mapping(source = "product", target = "product_id")
    @Mapping(source = "targetQuantity", target = "target_quantity")
    UsualPurchase toDomain(UsualPurchaseEntity entity);

    List<UsualPurchaseEntity> toEntityList(List<UsualPurchase> domainList);
    List<UsualPurchase> toDomainList(List<UsualPurchaseEntity> entityList);
}