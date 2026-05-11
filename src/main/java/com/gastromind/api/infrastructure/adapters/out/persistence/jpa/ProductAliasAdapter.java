package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.ProductAlias;
import com.gastromind.api.domain.ports.out.ProductAliasRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.ProductAliasMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.ProductAliasJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persiste sinónimos de producto y resolución por forma normalizada del texto.
 */
@Component
public class ProductAliasAdapter implements ProductAliasRepository {
    private final ProductAliasJpaRepository repository;
    private final ProductAliasMapper mapper;

    public ProductAliasAdapter(ProductAliasJpaRepository repository, ProductAliasMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProductAlias save(ProductAlias alias) {
        return mapper.toDomain(repository.save(mapper.toEntity(alias)));
    }

    @Override
    public Optional<ProductAlias> findFirstByAliasNorm(String aliasNorm) {
        return repository.findFirstByAliasNorm(aliasNorm).map(mapper::toDomain);
    }
}
