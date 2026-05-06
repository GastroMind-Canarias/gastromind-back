package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAliasJpaRepository extends JpaRepository<ProductAliasEntity, String> {
    Optional<ProductAliasEntity> findFirstByAliasNorm(String aliasNorm);
}
