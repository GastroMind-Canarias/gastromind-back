package com.gastromind.api.domain.ports.out;

public interface AliasRateLimitPort {
    boolean allowAliasCreation(String userId);
}
