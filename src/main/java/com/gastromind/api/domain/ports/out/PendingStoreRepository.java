package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.enums.PendingStoreStatus;

import java.util.List;
import java.util.Optional;

/**
 * Persistencia de tiendas candidatas antes de promoverlas al catálogo o rechazarlas.
 */
public interface PendingStoreRepository {
    PendingStore save(PendingStore pendingStore);

    Optional<PendingStore> findById(String id);

    Optional<PendingStore> findFirstByDetectedNameNormAndStatus(String detectedNameNorm, PendingStoreStatus status);

    List<PendingStore> findByStatus(PendingStoreStatus status);
}
