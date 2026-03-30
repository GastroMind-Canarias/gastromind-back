package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FridgeItemJpaRepository extends JpaRepository<FridgeItemEntity, String> {
    List<FridgeItemEntity> findByFridgeId(String fridgeId);

    List<FridgeItemEntity> findByFridgeIdAndExpirationDateBefore(String fridgeId, java.time.LocalDate thresholdDate);

    List<FridgeItemEntity> findByFridgeIdAndProductCategoryId(String fridgeId, String categoryId);
}
