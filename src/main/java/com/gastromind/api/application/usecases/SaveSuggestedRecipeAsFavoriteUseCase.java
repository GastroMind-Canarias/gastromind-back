package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.in.IRecipeService;
import com.gastromind.api.domain.ports.in.IUserFavoritesService;
import com.gastromind.api.domain.ports.out.RecipeIngredientWritePort;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Caso de uso que persiste una receta sugerida y la guarda en favoritos del usuario.
 * Tras guardarla, elimina la sugerencia de la cachA temporal.
 */
public class SaveSuggestedRecipeAsFavoriteUseCase {

    private final RecipeSuggestionCachePort suggestionCache;
    private final IRecipeService recipeService;
    private final IUserFavoritesService userFavoritesService;
    private final RecipeIngredientWritePort recipeIngredientWritePort;

    public SaveSuggestedRecipeAsFavoriteUseCase(
            RecipeSuggestionCachePort suggestionCache,
            IRecipeService recipeService,
            IUserFavoritesService userFavoritesService,
            RecipeIngredientWritePort recipeIngredientWritePort) {
        this.suggestionCache = suggestionCache;
        this.recipeService = recipeService;
        this.userFavoritesService = userFavoritesService;
        this.recipeIngredientWritePort = recipeIngredientWritePort;
    }
    /**
     * Guarda una sugerencia como receta definitiva y la aAade a favoritos.
     *
     * @param suggestionId identificador de la sugerencia en cachA
     * @param householdId identificador del hogar asociado
     * @param userId identificador del usuario propietario de la sugerencia
     * @param currentUser usuario que recibirA el favorito
     * @return registro de favorito creado
     * @throws NotFoundException si la sugerencia no existe o ha expirado
     */

    @Transactional
    public UserFavorites execute(String suggestionId, String householdId, String userId, User currentUser) {
        Recipe draft = suggestionCache.find(suggestionId, householdId, userId)
                .orElseThrow(() -> new NotFoundException("Sugerencia no encontrada o expirada"));

        draft.setId(null);
        Recipe savedRecipe = recipeService.create(draft);
        recipeIngredientWritePort.saveForRecipe(savedRecipe.getId(), draft.getIngredientsUsed());

        UserFavorites favorite = new UserFavorites();
        favorite.setUser_id(currentUser);
        favorite.setRecipe_id(savedRecipe);
        UserFavorites saved = userFavoritesService.create(favorite);

        suggestionCache.delete(suggestionId, householdId, userId);
        return saved;
    }
}




