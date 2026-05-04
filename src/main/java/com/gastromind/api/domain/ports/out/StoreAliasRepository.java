package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.StoreAlias;

import java.util.List;
import java.util.Optional;

public interface StoreAliasRepository {
    StoreAlias save(StoreAlias alias);

    Optional<StoreAlias> findFirstByAliasNorm(String aliasNorm);

    boolean existsByStoreIdAndAliasNorm(String storeId, String aliasNorm);

    List<StoreAlias> findByStoreId(String storeId);

    Optional<StoreAlias> findById(String id);

    void deleteById(String id);
}
