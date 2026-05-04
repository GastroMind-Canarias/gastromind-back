package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.domain.ports.out.StoreAliasRepository;
import com.gastromind.api.domain.ports.out.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreResolutionService {
    private final StoreRepository storeRepository;
    private final StoreAliasRepository aliasRepository;
    private final PendingStoreService pendingStoreService;
    private final StoreNameNormalizer normalizer;

    public StoreResolutionService(
            StoreRepository storeRepository,
            StoreAliasRepository aliasRepository,
            PendingStoreService pendingStoreService,
            StoreNameNormalizer normalizer) {
        this.storeRepository = storeRepository;
        this.aliasRepository = aliasRepository;
        this.pendingStoreService = pendingStoreService;
        this.normalizer = normalizer;
    }

    public StoreResolutionResult resolve(String storeIdOrNull, String extractedStoreName) {
        if (storeIdOrNull != null && !storeIdOrNull.isBlank()) {
            Store strict = storeRepository.findById(storeIdOrNull)
                    .orElseThrow(() -> new NotFoundException("Tienda no encontrada"));
            return new StoreResolutionResult(strict, null, extractedStoreName);
        }

        String norm = normalizer.normalize(extractedStoreName);
        if (norm.isEmpty()) {
            PendingStore pending = pendingStoreService.createOrUpdateSighting("Desconocida");
            return new StoreResolutionResult(null, pending, extractedStoreName);
        }

        Store official = storeRepository.findFirstByNameNorm(norm).orElse(null);
        if (official != null) {
            return new StoreResolutionResult(official, null, extractedStoreName);
        }

        StoreAlias alias = aliasRepository.findFirstByAliasNorm(norm).orElse(null);
        if (alias != null) {
            Store mapped = storeRepository.findById(alias.getStoreId()).orElse(null);
            if (mapped != null) {
                return new StoreResolutionResult(mapped, null, extractedStoreName);
            }
        }

        List<Store> byNorm = storeRepository.findByNameNorm(norm);
        if (byNorm.size() == 1) {
            return new StoreResolutionResult(byNorm.get(0), null, extractedStoreName);
        }

        PendingStore pending = pendingStoreService.createOrUpdateSighting(extractedStoreName == null ? "Desconocida" : extractedStoreName.trim());
        return new StoreResolutionResult(null, pending, extractedStoreName);
    }
}
