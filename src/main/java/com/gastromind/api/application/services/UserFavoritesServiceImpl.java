package com.gastromind.api.application.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.in.IUserFavoritesService;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.domain.ports.out.RecipeRepository;
import com.gastromind.api.domain.ports.out.UserFavoritesRepository;

@Service
public class UserFavoritesServiceImpl implements IUserFavoritesService {

    private final UserFavoritesRepository repository;
    private final IUserService userService;
    private final RecipeRepository recipeRepository;

    public UserFavoritesServiceImpl(UserFavoritesRepository repository,
            IUserService userService,
            RecipeRepository recipeRepository) {
        this.repository = repository;
        this.userService = userService;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<UserFavorites> findAll() {
        return repository.findAll();
    }

    @Override
    public UserFavorites findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Receta Favorita no encontrada"));
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

    /**
     * GuardarRecetaFavorita: crea un nuevo registro en user_favorites
     * vinculando user_id y recipe_id.
     */
    @Override
    @Transactional
    public UserFavorites addFavorite(String userId, String recipeId) {
        // Verificar que el usuario y la receta existen
        User user = userService.findById(userId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Receta no encontrada: " + recipeId));

        // Evitar duplicados
        repository.findByUserIdAndRecipeId(userId, recipeId).ifPresent(f -> {
            throw new IllegalStateException("Esta receta ya está en favoritos del usuario");
        });

        UserFavorites favorite = new UserFavorites();
        favorite.setUser_id(user);
        favorite.setRecipe_id(recipe);
        return repository.save(favorite);
    }

    /**
     * EliminarRecetaFavorita: elimina el registro de user_favorites
     * por userId y recipeId.
     */
    @Override
    @Transactional
    public void removeFavorite(String userId, String recipeId) {
        repository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new NotFoundException("Este favorito no existe para el usuario"));
        repository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    /**
     * ListarRecetasFavoritas: recupera las recetas basadas en los registros
     * de user_favorites para un user_id dado.
     */
    @Override
    public List<Recipe> findFavoritesByUserId(String userId) {
        // Verificar que el usuario existe
        userService.findById(userId);
        return repository.findByUserId(userId).stream()
                .map(UserFavorites::getRecipe_id)
                .collect(Collectors.toList());
    }
}