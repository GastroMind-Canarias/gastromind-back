package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.HouseHoldServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IRecipeService;
import com.gastromind.api.domain.ports.in.IUserFavoritesService;
import com.gastromind.api.domain.ports.out.RecipeIngredientWritePort;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecipeSuggestionAndPreferencesUseCasesTest {

    @Test
    void saveSuggestedRecipe_shouldPersistRecipeFavoriteAndDeleteCache() {
        RecipeSuggestionCachePort cache = mock(RecipeSuggestionCachePort.class);
        IRecipeService recipeService = mock(IRecipeService.class);
        IUserFavoritesService favoritesService = mock(IUserFavoritesService.class);
        RecipeIngredientWritePort ingredientWritePort = mock(RecipeIngredientWritePort.class);
        SaveSuggestedRecipeAsFavoriteUseCase useCase = new SaveSuggestedRecipeAsFavoriteUseCase(
                cache, recipeService, favoritesService, ingredientWritePort);

        Recipe draft = new Recipe();
        draft.setId("draft-id");
        when(cache.find("s-1", "h-1", "u-1")).thenReturn(Optional.of(draft));
        when(recipeService.create(any(Recipe.class))).thenAnswer(inv -> {
            Recipe r = inv.getArgument(0);
            r.setId("saved-r-1");
            return r;
        });
        UserFavorites savedFavorite = new UserFavorites();
        when(favoritesService.create(any(UserFavorites.class))).thenReturn(savedFavorite);

        User current = new User();
        current.setId("u-1");
        UserFavorites out = useCase.execute("s-1", "h-1", "u-1", current);

        assertEquals(savedFavorite, out);
        verify(recipeService).create(any(Recipe.class));
        verify(ingredientWritePort).saveForRecipe(eq("saved-r-1"), any());
        verify(favoritesService).create(any(UserFavorites.class));
        verify(cache).delete("s-1", "h-1", "u-1");
    }

    @Test
    void saveSuggestedRecipe_shouldFail_whenSuggestionNotFound() {
        RecipeSuggestionCachePort cache = mock(RecipeSuggestionCachePort.class);
        SaveSuggestedRecipeAsFavoriteUseCase useCase = new SaveSuggestedRecipeAsFavoriteUseCase(
                cache, mock(IRecipeService.class), mock(IUserFavoritesService.class), mock(RecipeIngredientWritePort.class));
        when(cache.find("s-404", "h-1", "u-1")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> useCase.execute("s-404", "h-1", "u-1", new User()));
        assertEquals("Sugerencia no encontrada o expirada", ex.getMessage());
    }

    @Test
    void getSuggestionFromCache_shouldDelegateFind() {
        RecipeSuggestionCachePort cache = mock(RecipeSuggestionCachePort.class);
        GetRecipeSuggestionFromCacheUseCase useCase = new GetRecipeSuggestionFromCacheUseCase(cache);
        Recipe recipe = new Recipe();
        recipe.setId("r-1");
        when(cache.find("s-1", "h-1", "u-1")).thenReturn(Optional.of(recipe));

        Optional<Recipe> out = useCase.execute("s-1", "h-1", "u-1");

        assertTrue(out.isPresent());
        assertEquals("r-1", out.get().getId());
        verify(cache).find("s-1", "h-1", "u-1");
    }

    @Test
    void updateMyPreferences_shouldRejectInvalidPrincipalAndRole() {
        UserServiceImpl userService = mock(UserServiceImpl.class);
        HouseHoldServiceImpl householdService = mock(HouseHoldServiceImpl.class);
        UpdateMyPreferencesUseCase useCase = new UpdateMyPreferencesUseCase(userService, householdService);

        ForbiddenException unauth = assertThrows(ForbiddenException.class,
                () -> useCase.execute(" ", List.of(), List.of()));
        assertEquals("Usuario no autenticado", unauth.getMessage());

        User member = new User();
        member.setId("u-1");
        HouseHold house = new HouseHold();
        house.setId("h-1");
        member.setHouseHold_id(house);
        member.setRole(Role.ROLE_MEMBER);
        when(userService.findByUsername("member")).thenReturn(member);

        ForbiddenException notOwner = assertThrows(ForbiddenException.class,
                () -> useCase.execute("member", List.of(), List.of()));
        assertEquals("Solo el OWNER del hogar puede gestionar los electrodomesticos", notOwner.getMessage());
        verify(userService, never()).replaceAllergens(any(), any());
    }

    @Test
    void updateMyPreferences_shouldReplaceAllergensAndAppliancesForOwner() {
        UserServiceImpl userService = mock(UserServiceImpl.class);
        HouseHoldServiceImpl householdService = mock(HouseHoldServiceImpl.class);
        UpdateMyPreferencesUseCase useCase = new UpdateMyPreferencesUseCase(userService, householdService);

        User owner = new User();
        owner.setId("u-1");
        HouseHold house = new HouseHold();
        house.setId("h-1");
        owner.setHouseHold_id(house);
        owner.setRole(Role.ROLE_OWNER);
        when(userService.findByUsername("owner")).thenReturn(owner);
        when(userService.findById("u-1")).thenReturn(owner);

        User out = useCase.execute("owner", List.of("a-1"), List.of(Appliance.HORNO));

        assertEquals("u-1", out.getId());
        verify(userService).replaceAllergens("u-1", List.of("a-1"));
        verify(householdService).replaceAppliances("h-1", List.of(Appliance.HORNO));
        verify(userService).findById("u-1");
    }
}
