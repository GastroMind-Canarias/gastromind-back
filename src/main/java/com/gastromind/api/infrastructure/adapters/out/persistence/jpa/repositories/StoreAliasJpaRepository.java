package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * CRUD y listados de alias por tienda canónica.
 */
public interface StoreAliasJpaRepository extends JpaRepository<StoreAliasEntity, String> {
    Optional<StoreAliasEntity> findFirstByAliasNorm(String aliasNorm);

    boolean existsByStore_IdAndAliasNorm(String storeId, String aliasNorm);

    List<StoreAliasEntity> findByStore_Id(String storeId);
}
