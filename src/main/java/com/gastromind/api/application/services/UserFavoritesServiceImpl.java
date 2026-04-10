package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.in.IUserFavoritesService;
import com.gastromind.api.domain.ports.out.UserFavoritesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoritesServiceImpl implements IUserFavoritesService {

    private final UserFavoritesRepository repository;


    public UserFavoritesServiceImpl(UserFavoritesRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserFavorites> findAll() {
        return repository.findAll();
    }

    @Override
    public List<UserFavorites> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public UserFavorites findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Receta Favorita no encontrada"));
    }

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

    @Override
    public UserFavorites create(UserFavorites userFavorites) {
        return repository.save(userFavorites);
    }

    @Override
    public UserFavorites update(String id, UserFavorites userFavorites) {
        findById(id);
        userFavorites.setId(id);
        return repository.save(userFavorites);
    }

    @Override
    public UserFavorites updateForUser(String id, UserFavorites userFavorites, String userId) {
        UserFavorites existing = findByIdForUser(id, userId);
        userFavorites.setId(id);
        userFavorites.setUser_id(existing.getUser_id());
        return repository.save(userFavorites);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public void deleteForUser(String id, String userId) {
        findByIdForUser(id, userId);
        repository.deleteById(id);
    }
}
