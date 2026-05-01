package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
/**
 * Caso de uso para recuperar una sugerencia de receta almacenada en cachA.
 */
public class GetRecipeSuggestionFromCacheUseCase {

    private final RecipeSuggestionCachePort suggestionCache;
    /**
     * Constructor con el puerto de acceso a cachA de sugerencias.
     *
     * @param suggestionCache puerto de lectura de sugerencias cacheadas
     */

    public GetRecipeSuggestionFromCacheUseCase(RecipeSuggestionCachePort suggestionCache) {
        this.suggestionCache = suggestionCache;
    }
    /**
     * Busca una sugerencia concreta para un usuario y hogar.
     *
     * @param suggestionId identificador de la sugerencia
     * @param householdId identificador del hogar
     * @param userId identificador del usuario
     * @return receta sugerida envuelta en {@code Optional} si existe en cachA
     */

    public Optional<Recipe> execute(String suggestionId, String householdId, String userId) {
        return suggestionCache.find(suggestionId, householdId, userId);
    }
}




