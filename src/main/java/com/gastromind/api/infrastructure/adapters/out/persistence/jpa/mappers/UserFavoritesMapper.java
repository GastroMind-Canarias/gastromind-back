package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class, RecipeMapper.class })
public interface UserFavoritesMapper {

    @Mapping(source = "user_id", target = "user")
    @Mapping(source = "recipe_id", target = "recipe")
    UserFavoritesEntity toEntity(UserFavorites domain);

    @Mapping(source = "user", target = "user_id")
    @Mapping(source = "recipe", target = "recipe_id")
    UserFavorites toDomain(UserFavoritesEntity entity);

    List<UserFavoritesEntity> toEntityList(List<UserFavorites> domainList);

    List<UserFavorites> toDomainList(List<UserFavoritesEntity> entityList);
}