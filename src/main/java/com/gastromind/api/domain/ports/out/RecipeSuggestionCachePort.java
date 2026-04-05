package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Recipe;

import java.util.Optional;

/**
 * Almacén temporal de sugerencias (p. ej. Redis) hasta que el usuario las guarde como favoritas.
 */
public interface RecipeSuggestionCachePort {

    /**
     * Guarda la receta sugerida y devuelve el id de sugerencia para el cliente.
     */
    String save(String householdId, String userId, Recipe recipe);

    /**
     * Recupera la sugerencia si existe y coincide hogar + usuario.
     */
    Optional<Recipe> find(String suggestionId, String householdId, String userId);

    void delete(String suggestionId, String householdId, String userId);
}
