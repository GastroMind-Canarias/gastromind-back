package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.domain.ports.out.StoreAliasRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.StoreAliasMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.StoreAliasJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persiste alias de tienda enlazados al {@code store_id} oficial.
 */
@Component
public class StoreAliasAdapter implements StoreAliasRepository {
    private final StoreAliasJpaRepository repository;
    private final StoreAliasMapper mapper;

    public StoreAliasAdapter(StoreAliasJpaRepository repository, StoreAliasMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public StoreAlias save(StoreAlias alias) {
        return mapper.toDomain(repository.save(mapper.toEntity(alias)));
    }

    @Override
    public Optional<StoreAlias> findFirstByAliasNorm(String aliasNorm) {
        return repository.findFirstByAliasNorm(aliasNorm).map(mapper::toDomain);
    }

    @Override
    public boolean existsByStoreIdAndAliasNorm(String storeId, String aliasNorm) {
        return repository.existsByStore_IdAndAliasNorm(storeId, aliasNorm);
    }

    @Override
    public List<StoreAlias> findByStoreId(String storeId) {
        return mapper.toDomainList(repository.findByStore_Id(storeId));
    }

    @Override
    public Optional<StoreAlias> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
