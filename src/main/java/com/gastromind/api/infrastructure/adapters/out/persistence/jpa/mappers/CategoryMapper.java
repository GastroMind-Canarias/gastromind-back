package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.CategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryEntity toEntity(Category domain);

    Category toDomain(CategoryEntity entity);

    List<CategoryEntity> toEntityList(List<Category> domainList);

    List<Category> toDomainList(List<CategoryEntity> entityList);
}