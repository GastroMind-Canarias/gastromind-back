package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserFavoritesRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user_id.id", source = "user_id")
    @Mapping(target = "recipe_id.id", source = "recipe_id")
    UserFavorites toDomain(UserFavoritesRequest request);

    @Mapping(target = "user_id", source = "user_id.id")
    @Mapping(target = "recipe_id", source = "recipe_id.id")
    UserFavoritesResponse toResponse(UserFavorites domain);

    List<UserFavoritesResponse> toResponseList(List<UserFavorites> userFavorites);

    default UserFavorites toDomainForMe(UserFavoritesMeRequest request, String userId) {
        UserFavorites uf = new UserFavorites();
        uf.setUser_id(new User(userId));
        uf.setRecipe_id(new Recipe(request.recipe_id()));
        return uf;
    }
}
