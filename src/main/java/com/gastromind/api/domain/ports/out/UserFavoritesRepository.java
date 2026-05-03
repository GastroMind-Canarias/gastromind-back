package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.UserFavorites;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para user favorites.
 */
public interface UserFavoritesRepository {
    UserFavorites save(UserFavorites userFavorites);

    Optional<UserFavorites> findById(String id);

    void deleteById(String id);

    List<UserFavorites> findAll();

    List<UserFavorites> findAllByUserId(String userId);
}
