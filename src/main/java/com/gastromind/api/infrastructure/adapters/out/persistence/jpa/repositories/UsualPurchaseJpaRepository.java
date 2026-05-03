package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Define el contrato de usual purchase jpa.
 */
public interface UsualPurchaseJpaRepository extends JpaRepository<UsualPurchaseEntity, String> {

    List<UsualPurchaseEntity> findByUser_Id(String userId);

    Optional<UsualPurchaseEntity> findByUser_IdAndProduct_Id(String userId, String productId);
}






