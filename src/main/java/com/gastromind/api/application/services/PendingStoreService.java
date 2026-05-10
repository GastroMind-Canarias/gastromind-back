package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.enums.PendingStoreStatus;
import com.gastromind.api.domain.ports.out.PendingStoreCachePort;
import com.gastromind.api.domain.ports.out.PendingStoreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Orquesta avistamientos de tiendas no resueltas: normaliza el nombre, fusiona en pendientes y avisa a caché.
 */
@Service
public class PendingStoreService {
    private final PendingStoreRepository repository;
    private final PendingStoreCachePort cachePort;
    private final StoreNameNormalizer normalizer;

    public PendingStoreService(PendingStoreRepository repository, PendingStoreCachePort cachePort, StoreNameNormalizer normalizer) {
        this.repository = repository;
        this.cachePort = cachePort;
        this.normalizer = normalizer;
    }

    public PendingStore createOrUpdateSighting(String detectedStoreName) {
        String normalized = normalizer.normalize(detectedStoreName);
        LocalDateTime now = LocalDateTime.now();
        PendingStore pending = repository.findFirstByDetectedNameNormAndStatus(normalized, PendingStoreStatus.OPEN)
                .orElseGet(PendingStore::new);
        if (pending.getId() == null) {
            pending.setDetectedName(detectedStoreName);
            pending.setDetectedNameNorm(normalized);
            pending.setStatus(PendingStoreStatus.OPEN);
            pending.setFirstSeenAt(now);
            pending.setSightingsCount(0);
        }
        pending.setLastSeenAt(now);
        pending.setSightingsCount(pending.getSightingsCount() + 1);
        PendingStore saved = repository.save(pending);
        cachePort.rememberPendingSighting(normalized);
        return saved;
    }

    public List<PendingStore> listOpen() {
        return repository.findByStatus(PendingStoreStatus.OPEN);
    }

    public PendingStore reject(String pendingId, String reason) {
        PendingStore pending = repository.findById(pendingId).orElseThrow(() -> new NotFoundException("Pending store no encontrado"));
        pending.setStatus(PendingStoreStatus.REJECTED);
        pending.setRejectionReason(reason);
        return repository.save(pending);
    }

    public PendingStore promote(String pendingId, Store store) {
        PendingStore pending = repository.findById(pendingId).orElseThrow(() -> new NotFoundException("Pending store no encontrado"));
        pending.setStatus(PendingStoreStatus.PROMOTED);
        pending.setResolvedStoreId(store.getId());
        pending.setRejectionReason(null);
        return repository.save(pending);
    }
}
