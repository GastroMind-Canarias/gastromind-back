package com.gastromind.api.infrastructure.adapters.out.cache;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisAliasRateLimitAdapterTest {
    @Test
    void allowAliasCreation_shouldBlockWhenExceedsLimit() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment("gastromind:store:alias:rate:u-1")).thenReturn(9L);

        StoreFlowProperties properties = new StoreFlowProperties();
        RedisAliasRateLimitAdapter adapter = new RedisAliasRateLimitAdapter(redisTemplate, properties);
        assertFalse(adapter.allowAliasCreation("u-1"));
    }

    @Test
    void allowAliasCreation_shouldAllowWithinLimit() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment("gastromind:store:alias:rate:u-1")).thenReturn(1L);

        StoreFlowProperties properties = new StoreFlowProperties();
        RedisAliasRateLimitAdapter adapter = new RedisAliasRateLimitAdapter(redisTemplate, properties);
        assertTrue(adapter.allowAliasCreation("u-1"));
    }
}
