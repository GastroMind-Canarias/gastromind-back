package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.UserFavorites;

public interface UserFavoritesRepository {
    UserFavorites save(UserFavorites userFavorites);

    Optional<UserFavorites> findById(String id);

    void deleteById(String id);

    List<UserFavorites> findAll();

    /** Busca todos los favoritos de un usuario por su userId */
    List<UserFavorites> findByUserId(String userId);

    /** Busca una entrada concreta de favorito por userId y recipeId */
    Optional<UserFavorites> findByUserIdAndRecipeId(String userId, String recipeId);

    /** Elimina por userId y recipeId */
    void deleteByUserIdAndRecipeId(String userId, String recipeId);
}
