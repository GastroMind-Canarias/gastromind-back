package com.gastromind.api.infrastructure.adapters.out.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Prefijos Redis y TTL para cola de tiendas pendientes y rate-limit de alias ({@code app.store.*}).
 */
@ConfigurationProperties(prefix = "app.store")
public class StoreFlowProperties {
    private PendingCache pendingCache = new PendingCache();
    private AliasRateLimit aliasRateLimit = new AliasRateLimit();

    public PendingCache getPendingCache() {
        return pendingCache;
    }

    public void setPendingCache(PendingCache pendingCache) {
        this.pendingCache = pendingCache;
    }

    public AliasRateLimit getAliasRateLimit() {
        return aliasRateLimit;
    }

    public void setAliasRateLimit(AliasRateLimit aliasRateLimit) {
        this.aliasRateLimit = aliasRateLimit;
    }

    public static class PendingCache {
        private int ttlDays = 5;
        private String keyPrefix = "gastromind:store:pending:";

        public int getTtlDays() {
            return ttlDays;
        }

        public void setTtlDays(int ttlDays) {
            this.ttlDays = ttlDays;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }
    }

    public static class AliasRateLimit {
        private int maxAttempts = 8;
        private int windowSeconds = 3600;
        private String keyPrefix = "gastromind:store:alias:rate:";

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public int getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(int windowSeconds) {
            this.windowSeconds = windowSeconds;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }
    }
}
