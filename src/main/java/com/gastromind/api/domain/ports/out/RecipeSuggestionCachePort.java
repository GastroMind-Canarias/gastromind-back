package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Recipe;

import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para recipe suggestion cache.
 */
public interface RecipeSuggestionCachePort {

    String save(String householdId, String userId, Recipe recipe);

    Optional<Recipe> find(String suggestionId, String householdId, String userId);

    void delete(String suggestionId, String householdId, String userId);
}
