package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.domain.models.enums.PendingStoreStatus;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.PendingStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PendingStoreJpaRepository extends JpaRepository<PendingStoreEntity, String> {
    Optional<PendingStoreEntity> findFirstByDetectedNameNormAndStatusOrderByLastSeenAtDesc(
            String detectedNameNorm,
            PendingStoreStatus status);

    List<PendingStoreEntity> findByStatusOrderByLastSeenAtDesc(PendingStoreStatus status);
}
