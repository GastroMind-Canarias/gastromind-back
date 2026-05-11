package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.UserFavorites;

import java.util.List;
import java.util.Optional;

/**
 * Recetas marcadas como favoritas por usuario para listados rápidos.
 */
public interface UserFavoritesRepository {
    UserFavorites save(UserFavorites userFavorites);

    Optional<UserFavorites> findById(String id);

    void deleteById(String id);

    List<UserFavorites> findAll();

    List<UserFavorites> findAllByUserId(String userId);
}
