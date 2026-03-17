package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface ProductMapper {

    @Mapping(source = "is_essential", target = "isEssential")
    ProductEntity toEntity(Product domain);

    @Mapping(source = "isEssential", target = "is_essential")
    Product toDomain(ProductEntity entity);

    List<ProductEntity> toEntityList(List<Product> domainList);

    List<Product> toDomainList(List<ProductEntity> entityList);
}