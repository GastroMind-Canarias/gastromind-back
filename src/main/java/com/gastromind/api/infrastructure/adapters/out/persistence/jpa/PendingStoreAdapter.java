package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.enums.PendingStoreStatus;
import com.gastromind.api.domain.ports.out.PendingStoreRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.PendingStoreMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.PendingStoreJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PendingStoreAdapter implements PendingStoreRepository {
    private final PendingStoreJpaRepository repository;
    private final PendingStoreMapper mapper;

    public PendingStoreAdapter(PendingStoreJpaRepository repository, PendingStoreMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PendingStore save(PendingStore pendingStore) {
        return mapper.toDomain(repository.save(mapper.toEntity(pendingStore)));
    }

    @Override
    public Optional<PendingStore> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<PendingStore> findFirstByDetectedNameNormAndStatus(String detectedNameNorm, PendingStoreStatus status) {
        return repository.findFirstByDetectedNameNormAndStatusOrderByLastSeenAtDesc(detectedNameNorm, status)
                .map(mapper::toDomain);
    }

    @Override
    public List<PendingStore> findByStatus(PendingStoreStatus status) {
        return mapper.toDomainList(repository.findByStatusOrderByLastSeenAtDesc(status));
    }
}
