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
     * Persiste y vuelve a leer con grafo receta/ingredientes para que la API devuelva cuerpo completo
     * (p. ej. tras guardar lineas en {@code recipe_ingredients}).
     */
    @Override
    public UserFavorites save(UserFavorites userFavorites) {
        UserFavoritesEntity entity = userFavoritesMapper.toEntity(userFavorites);
        UserFavoritesEntity saved = userFavoritesJpaRepository.save(entity);
        return userFavoritesJpaRepository.findDetailedById(saved.getId())
                .map(userFavoritesMapper::toDomain)
                .orElseGet(() -> userFavoritesMapper.toDomain(saved));
    }

    @Override
    public Optional<UserFavorites> findById(String id) {
        return userFavoritesJpaRepository.findDetailedById(id).map(userFavoritesMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        userFavoritesJpaRepository.deleteById(id);
    }

    @Override
    public List<UserFavorites> findAll() {
        return userFavoritesMapper.toDomainList(userFavoritesJpaRepository.findAllDetailed());
    }

    @Override
    public List<UserFavorites> findAllByUserId(String userId) {
        return userFavoritesMapper.toDomainList(userFavoritesJpaRepository.findDetailedByUserId(userId));
    }
}
