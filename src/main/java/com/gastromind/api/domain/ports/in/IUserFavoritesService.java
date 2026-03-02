package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.UserFavorites;

public interface IUserFavoritesService {
    List<UserFavorites> findAll();

    UserFavorites findById(String id);

    UserFavorites create(UserFavorites userFavorites);

    UserFavorites update(String id, UserFavorites userFavorites);

    void delete(String id);

    /** GuardarRecetaFavorita: vincula user_id + recipe_id en user_favorites */
    UserFavorites addFavorite(String userId, String recipeId);

    /**
     * EliminarRecetaFavorita: elimina la entrada de user_favorites por userId y
     * recipeId
     */
    void removeFavorite(String userId, String recipeId);

    /**
     * ListarRecetasFavoritas: recupera todas las recetas favoritas de un usuario
     */
    List<Recipe> findFavoritesByUserId(String userId);
}