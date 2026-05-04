package com.gastromind.api.infrastructure.adapters.out.cache;

import com.gastromind.api.domain.ports.out.AliasRateLimitPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisAliasRateLimitAdapter implements AliasRateLimitPort {
    private final StringRedisTemplate redisTemplate;
    private final StoreFlowProperties properties;

    public RedisAliasRateLimitAdapter(StringRedisTemplate redisTemplate, StoreFlowProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public boolean allowAliasCreation(String userId) {
        String key = properties.getAliasRateLimit().getKeyPrefix() + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(Math.max(1, properties.getAliasRateLimit().getWindowSeconds())));
        }
        return count != null && count <= properties.getAliasRateLimit().getMaxAttempts();
    }
}
