package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.out.UserFavoritesRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UserFavoritesMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserFavoritesJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa user favorites dentro del dominio de la aplicacion.
 */
public class UserFavoritesAdapter implements UserFavoritesRepository {

    @Autowired
    UserFavoritesJpaRepository userFavoritesJpaRepository;

    @Autowired
    UserFavoritesMapper userFavoritesMapper;
    /**
     * Registra un nuevo user favorites.
     * @param userFavorites valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public UserFavorites save(UserFavorites userFavorites) {
         UserFavoritesEntity entity = userFavoritesMapper.toEntity(userFavorites);
        return userFavoritesMapper.toDomain(userFavoritesJpaRepository.save(entity));
    }
    /**
     * Devuelve user favorites por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<UserFavorites> findById(String id) {
        return userFavoritesJpaRepository.findById(id).map(userFavoritesMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        userFavoritesJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los user favorites.
     * @return lista actual.
     */

    @Override
    public List<UserFavorites> findAll() {
        List<UserFavoritesEntity> userFavoritesEntities = userFavoritesJpaRepository.findAll();
        return userFavoritesMapper.toDomainList(userFavoritesEntities);
    }
    /**
     * Realiza find all by user id.
     * @param userId el identificador del usuario
     * @return lista actual.
     */

    @Override
    public List<UserFavorites> findAllByUserId(String userId) {
        return userFavoritesMapper.toDomainList(userFavoritesJpaRepository.findByUser_Id(userId));
    }
}




