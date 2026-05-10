package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
/**
 * Define el contrato de product.
 */
public interface ProductMapper {

    @Mapping(source = "is_essential", target = "isEssential")
    @Mapping(target = "needsReview", expression = "java(domain.isNeedsReview())")
    @Mapping(source = "category", target = "category")
    ProductEntity toEntity(Product domain);

    @Mapping(source = "isEssential", target = "is_essential")
    @Mapping(target = "needsReview", expression = "java(Boolean.TRUE.equals(entity.getNeedsReview()))")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "allergen", ignore = true)
    Product toDomain(ProductEntity entity);

    List<ProductEntity> toEntityList(List<Product> domainList);

    List<Product> toDomainList(List<ProductEntity> entityList);
}






