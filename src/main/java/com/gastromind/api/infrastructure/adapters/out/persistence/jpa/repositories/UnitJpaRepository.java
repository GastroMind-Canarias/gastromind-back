package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Define el contrato de unit jpa.
 */
public interface UnitJpaRepository extends JpaRepository<UnitEntity,String> {

    java.util.Optional<UnitEntity> findByName(String name);

    java.util.Optional<UnitEntity> findFirstByNameOrderByIdAsc(String name);

    java.util.Optional<UnitEntity> findFirstByNameIgnoreCase(String name);
}






