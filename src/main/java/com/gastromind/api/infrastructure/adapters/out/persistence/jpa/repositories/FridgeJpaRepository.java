package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeJpaRepository extends JpaRepository<FridgeEntity,String>{

    List<FridgeEntity> findByHousehold_Id(String householdId);

    Optional<FridgeEntity> findFirstByHousehold_IdOrderByIdAsc(String householdId);
}
