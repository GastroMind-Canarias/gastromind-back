package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserFavoritesMapper {

    UserFavoritesEntity toEntity(UserFavorites domain);

    UserFavorites toDomain(UserFavoritesEntity entity);

    List<UserFavoritesEntity> toEntityList(List<UserFavorites> domainList);

    List<UserFavorites> toDomainList(List<UserFavoritesEntity> entityList);
}