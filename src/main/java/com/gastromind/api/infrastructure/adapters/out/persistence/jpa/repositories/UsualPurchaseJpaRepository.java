package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsualPurchaseJpaRepository extends JpaRepository<UsualPurchaseEntity, String> {

    @Query("SELECT up FROM UsualPurchaseEntity up WHERE up.user.id = :userId ORDER BY up.targetQuantity DESC")
    List<UsualPurchaseEntity> findByUserIdOrderByFrequencyDesc(@Param("userId") String userId);

    @Query("SELECT up FROM UsualPurchaseEntity up WHERE up.user.id = :userId AND up.product.id = :productId")
    Optional<UsualPurchaseEntity> findByUserIdAndProductId(@Param("userId") String userId,
            @Param("productId") String productId);
}
