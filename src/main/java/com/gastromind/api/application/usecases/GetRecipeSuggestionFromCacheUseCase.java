package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetRecipeSuggestionFromCacheUseCase {

    private final RecipeSuggestionCachePort suggestionCache;

    public GetRecipeSuggestionFromCacheUseCase(RecipeSuggestionCachePort suggestionCache) {
        this.suggestionCache = suggestionCache;
    }

    public Optional<Recipe> execute(String suggestionId, String householdId, String userId) {
        return suggestionCache.find(suggestionId, householdId, userId);
    }
}
