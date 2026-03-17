package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.in.IUserFavoritesService;
import com.gastromind.api.domain.ports.out.UserFavoritesRepository;

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
    public UserFavorites findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Receta Favorita no encontrada"));
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
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}