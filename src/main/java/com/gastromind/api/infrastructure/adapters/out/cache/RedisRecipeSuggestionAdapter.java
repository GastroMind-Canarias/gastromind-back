package com.gastromind.api.infrastructure.adapters.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisRecipeSuggestionAdapter implements RecipeSuggestionCachePort {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final RecipeSuggestionCacheProperties properties;

    public RedisRecipeSuggestionAdapter(
            StringRedisTemplate stringRedisTemplate,
            ObjectMapper objectMapper,
            RecipeSuggestionCacheProperties properties) {
        this.redis = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public String save(String householdId, String userId, Recipe recipe) {
        String id = UUID.randomUUID().toString();
        String key = properties.getKeyPrefix() + id;
        try {
            StoredSuggestion payload = new StoredSuggestion(householdId, userId, recipe);
            redis.opsForValue().set(key, objectMapper.writeValueAsString(payload),
                    Duration.ofDays(Math.max(1, properties.getTtlDays())));
            return id;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo guardar la sugerencia en caché", e);
        }
    }

    @Override
    public Optional<Recipe> find(String suggestionId, String householdId, String userId) {
        String key = properties.getKeyPrefix() + suggestionId;
        String json = redis.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            StoredSuggestion stored = objectMapper.readValue(json, StoredSuggestion.class);
            if (!stored.householdId().equals(householdId) || !stored.userId().equals(userId)) {
                return Optional.empty();
            }
            return Optional.ofNullable(stored.recipe());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String suggestionId, String householdId, String userId) {
        Optional<Recipe> r = find(suggestionId, householdId, userId);
        if (r.isEmpty()) {
            return;
        }
        redis.delete(properties.getKeyPrefix() + suggestionId);
    }

    private record StoredSuggestion(String householdId, String userId, Recipe recipe) {}
}
