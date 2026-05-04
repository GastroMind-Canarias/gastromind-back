package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Define el contrato de store jpa.
 */
public interface StoreJpaRepository extends JpaRepository<StoreEntity, String> {

    java.util.Optional<StoreEntity> findFirstByNameIgnoreCase(String name);

    java.util.Optional<StoreEntity> findFirstByNameNorm(String nameNorm);

    java.util.List<StoreEntity> findByNameNorm(String nameNorm);
}






