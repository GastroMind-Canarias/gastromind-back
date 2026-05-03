package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.in.IUserFavoritesService;
import com.gastromind.api.domain.ports.out.UserFavoritesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar recetas favoritas de usuario.
 */
public class UserFavoritesServiceImpl implements IUserFavoritesService {

    private final UserFavoritesRepository repository;
    /**
     * Crea el servicio con el repositorio de favoritos.
     * @param repository repositorio de favoritos de usuario
     */


    public UserFavoritesServiceImpl(UserFavoritesRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todos los favoritos registrados.
     * @return listado completo de favoritos
     */

    @Override
    public List<UserFavorites> findAll() {
        return repository.findAll();
    }
    /**
     * Devuelve los favoritos pertenecientes a un usuario.
     * @param userId identificador del usuario
     * @return favoritos del usuario indicado
     */

    @Override
    public List<UserFavorites> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }
    /**
     * Busca un favorito por su identificador.
     * @param id identificador del favorito
     * @return favorito encontrado
     * @throws NotFoundException si no existe el favorito
     */

    @Override
    public UserFavorites findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Receta Favorita no encontrada"));
    }
    /**
     * Busca un favorito y valida que pertenezca al usuario indicado.
     * @param id identificador del favorito
     * @param userId identificador del usuario
     * @return favorito encontrado y autorizado
     * @throws ForbiddenException si el favorito no pertenece al usuario
     */

    @Override
    public UserFavorites findByIdForUser(String id, String userId) {
        UserFavorites fav = findById(id);
        requireFavoriteOwner(fav, userId);
        return fav;
    }

    private static void requireFavoriteOwner(UserFavorites favorite, String userId) {
        if (favorite.getUser_id() == null || favorite.getUser_id().getId() == null
                || !favorite.getUser_id().getId().equals(userId)) {
            throw new ForbiddenException("No tiene acceso a este favorito");
        }
    }
    /**
     * Registra un nuevo favorito de usuario.
     * @param userFavorites datos del favorito a crear
     * @return favorito persistido
     */

    @Override
    public UserFavorites create(UserFavorites userFavorites) {
        return repository.save(userFavorites);
    }
    /**
     * Define un favorito existente.
     * @param id identificador del favorito
     * @param userFavorites nuevos datos del favorito
     * @return favorito actualizado
     */

    @Override
    public UserFavorites update(String id, UserFavorites userFavorites) {
        findById(id);
        userFavorites.setId(id);
        return repository.save(userFavorites);
    }
    /**
     * Define un favorito validando su pertenencia al usuario.
     * @param id identificador del favorito
     * @param userFavorites nuevos datos del favorito
     * @param userId identificador del usuario
     * @return favorito actualizado
     */

    @Override
    public UserFavorites updateForUser(String id, UserFavorites userFavorites, String userId) {
        UserFavorites existing = findByIdForUser(id, userId);
        userFavorites.setId(id);
        userFavorites.setUser_id(existing.getUser_id());
        return repository.save(userFavorites);
    }
    /**
     * Elimina un favorito por su identificador.
     * @param id identificador del favorito
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
    /**
     * Elimina un favorito validando que pertenezca al usuario.
     * @param id identificador del favorito
     * @param userId identificador del usuario
     */

    @Override
    public void deleteForUser(String id, String userId) {
        findByIdForUser(id, userId);
        repository.deleteById(id);
    }
}




