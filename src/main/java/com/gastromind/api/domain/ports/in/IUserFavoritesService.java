package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.UserFavorites;

import java.util.List;

/**
 * Define las operaciones de negocio para favoritos del usuario.
 */
public interface IUserFavoritesService {
    List<UserFavorites> findAll();

    List<UserFavorites> findAllByUserId(String userId);

    UserFavorites findById(String id);

    UserFavorites findByIdForUser(String id, String userId);

    UserFavorites create(UserFavorites userFavorites);

    UserFavorites update(String id, UserFavorites userFavorites);

    UserFavorites updateForUser(String id, UserFavorites userFavorites, String userId);

    void delete(String id);

    void deleteForUser(String id, String userId);
}
