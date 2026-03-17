package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(source = "prep_time", target = "prepTimeMinutes")
    @Mapping(source = "appliance_needed", target = "applianceNeeded")
    @Mapping(source = "created_at", target = "createdAt")
    RecipeEntity toEntity(Recipe domain);

    @Mapping(source = "prepTimeMinutes", target = "prep_time")
    @Mapping(source = "applianceNeeded", target = "appliance_needed")
    @Mapping(source = "createdAt", target = "created_at")
    Recipe toDomain(RecipeEntity entity);

    List<RecipeEntity> toEntityList(List<Recipe> domainList);
    List<Recipe> toDomainList(List<RecipeEntity> entityList);
}