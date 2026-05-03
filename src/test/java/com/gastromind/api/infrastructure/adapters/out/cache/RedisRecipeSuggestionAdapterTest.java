package com.gastromind.api.infrastructure.adapters.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.domain.models.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisRecipeSuggestionAdapterTest {

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RecipeSuggestionCacheProperties properties;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private RedisRecipeSuggestionAdapter adapter;

    @Test
    void save_returnsId() throws Exception {
        when(properties.getKeyPrefix()).thenReturn("pre:");
        when(properties.getTtlDays()).thenReturn(7);
        when(redis.opsForValue()).thenReturn(valueOps);
        Recipe r = new Recipe("rid");
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        String id = adapter.save("h1", "u1", r);
        assertEquals(36, id.length());
        verify(valueOps).set(anyString(), anyString(), any(java.time.Duration.class));
    }

    @Test
    void find_emptyWhenNoKey() {
        when(properties.getKeyPrefix()).thenReturn("pre:");
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("pre:sid")).thenReturn(null);
        assertTrue(adapter.find("sid", "h1", "u1").isEmpty());
    }
}
