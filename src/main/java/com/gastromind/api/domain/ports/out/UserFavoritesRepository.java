package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.UserFavorites;

public interface UserFavoritesRepository {
    /**
     * Guarda un usuario
     * 
     * @param userFavorites usuario a guardar
     * @return El usuario guardado
     */
    UserFavorites save(UserFavorites userFavorites);

    /**
     * Busca un UserFavorites por id
     * 
     * @param id id del usuairo
     * @return El usuario o null
     */
    Optional<UserFavorites> findById(String id);

    /**
     * Borra el usuario
     * 
     * @param id Id del usuario
     */
    void deleteById(String id);

    /**
     * Lista todos los UserFavorites
     * 
     * @return Lista con todos los usuarios
     */
    List<UserFavorites> findAll();

}
