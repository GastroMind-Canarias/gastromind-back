package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitJpaRepository extends JpaRepository<UnitEntity,String> {

    java.util.Optional<UnitEntity> findByName(String name);

    /** Reserva: si existiera datos legacy duplicados antes de aplicar UNIQUE(name). */
    java.util.Optional<UnitEntity> findFirstByNameOrderByIdAsc(String name);

    java.util.Optional<UnitEntity> findFirstByNameIgnoreCase(String name);
}
