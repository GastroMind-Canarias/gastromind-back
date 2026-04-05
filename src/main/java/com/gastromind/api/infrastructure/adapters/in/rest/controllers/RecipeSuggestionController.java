package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.usecases.GetRecipeSuggestionFromCacheUseCase;
import com.gastromind.api.application.usecases.SuggestRecipeFromHouseholdUseCase;
import com.gastromind.api.application.usecases.SuggestRecipeFromHouseholdUseCase.SuggestRecipeResult;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.SuggestRecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.SuggestRecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.RecipeRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/households")
@Tag(name = "Recetas IA", description = "Sugerencias de receta según nevera, alérgenos y electrodomésticos del hogar.")
public class RecipeSuggestionController {

    @Autowired
    private SuggestRecipeFromHouseholdUseCase suggestRecipeFromHouseholdUseCase;
    @Autowired
    private GetRecipeSuggestionFromCacheUseCase getRecipeSuggestionFromCacheUseCase;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private RecipeRestMapper recipeRestMapper;

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        if (authentication == null) {
            throw new ForbiddenException("Usuario no autenticado");
        }
        return userServiceImpl.findByUsername(authentication.getName());
    }

    private String requireHouseholdId(User user) {
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }
        return user.getHouseHold_id().getId();
    }

    @Operation(summary = "Sugerir una receta con IA (contexto del hogar)", description = """
            Genera **una** receta usando Gemini con productos de la nevera, alérgenos agregados de los miembros,
            electrodomésticos del hogar y raciones: o las indicadas en el cuerpo o, si no, el número de miembros.
            La sugerencia se guarda en Redis (~10 días) hasta guardarla como favorita.""")
    @ApiPostDoc
    @PostMapping("/me/recipes/suggestions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuggestRecipeResponse> suggest(
            Authentication authentication,
            @Valid @RequestBody(required = false) SuggestRecipeRequest request) {
        User user = getCurrentUser(authentication);
        String householdId = requireHouseholdId(user);
        Integer servings = request != null ? request.servings() : null;

        SuggestRecipeResult result = suggestRecipeFromHouseholdUseCase.execute(
                householdId, user.getId(), servings);

        RecipeResponse recipeResponse = recipeRestMapper.toResponse(result.recipe());
        return ResponseEntity.ok(new SuggestRecipeResponse(result.suggestionId(), recipeResponse));
    }

    @Operation(summary = "Recuperar una sugerencia guardada en caché", description = """
            Devuelve la misma forma que POST /suggestions si la clave sigue en Redis (TTL ~10 días)
            y coincide hogar + usuario. Útil si el front guarda solo suggestionId (p. ej. en la URL) y recarga la página.""")
    @ApiStandardDoc
    @GetMapping("/me/recipes/suggestions/{suggestionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuggestRecipeResponse> getSuggestion(
            Authentication authentication,
            @PathVariable String suggestionId) {
        User user = getCurrentUser(authentication);
        String householdId = requireHouseholdId(user);
        Recipe recipe = getRecipeSuggestionFromCacheUseCase
                .execute(suggestionId, householdId, user.getId())
                .orElseThrow(() -> new NotFoundException("Sugerencia no encontrada o expirada"));
        return ResponseEntity.ok(new SuggestRecipeResponse(suggestionId, recipeRestMapper.toResponse(recipe)));
    }
}
