package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsualPurchaseJpaRepository extends JpaRepository<UsualPurchaseEntity, String> {

    List<UsualPurchaseEntity> findByUser_Id(String userId);
}
