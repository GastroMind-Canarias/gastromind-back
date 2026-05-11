package com.gastromind.api.infrastructure.adapters.out.cache;

import com.gastromind.api.domain.ports.out.PendingStoreCachePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Dedupe ligero en Redis cuando reaparece el mismo nombre de tienda normalizado en días recientes.
 */
@Component
public class RedisPendingStoreCacheAdapter implements PendingStoreCachePort {
    private final StringRedisTemplate redisTemplate;
    private final StoreFlowProperties properties;

    public RedisPendingStoreCacheAdapter(StringRedisTemplate redisTemplate, StoreFlowProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public void rememberPendingSighting(String detectedNameNorm) {
        String key = properties.getPendingCache().getKeyPrefix() + detectedNameNorm;
        redisTemplate.opsForValue().set(key, "1", Duration.ofDays(Math.max(1, properties.getPendingCache().getTtlDays())));
    }
}
