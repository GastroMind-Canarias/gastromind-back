package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseHoldJpaRepository extends JpaRepository<HouseholdEntity, String> {
    @EntityGraph(attributePaths = {"members", "fridges"})
    Optional<HouseholdEntity> findById(String id);
}
