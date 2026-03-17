package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.UserFavorites;

public interface IUserFavoritesService {
    List<UserFavorites> findAll();
    UserFavorites findById(String id);
    UserFavorites create(UserFavorites userFavorites);
    UserFavorites update(String id, UserFavorites userFavorites);
    void delete(String id);
}