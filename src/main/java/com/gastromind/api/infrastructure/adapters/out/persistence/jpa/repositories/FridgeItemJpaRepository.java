package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
/**
 * Persistencia de líneas de nevera. Las consultas que devuelven listas hacen fetch de producto y categoría juntos para evitar el típico N+1 al montar respuestas con datos de catálogo.
 */
public interface FridgeItemJpaRepository extends JpaRepository<FridgeItemEntity, String> {

    @Query("""
            SELECT DISTINCT fi FROM FridgeItemEntity fi
            LEFT JOIN FETCH fi.product p
            LEFT JOIN FETCH p.category
            WHERE fi.fridge.id = :fridgeId
            """)
    List<FridgeItemEntity> findByFridgeId(@Param("fridgeId") String fridgeId);

    @Query("""
            SELECT DISTINCT fi FROM FridgeItemEntity fi
            LEFT JOIN FETCH fi.product p
            LEFT JOIN FETCH p.category
            WHERE fi.fridge.id = :fridgeId
            AND fi.expirationDate < :thresholdDate
            """)
    List<FridgeItemEntity> findByFridgeIdAndExpirationDateBefore(
            @Param("fridgeId") String fridgeId,
            @Param("thresholdDate") LocalDate thresholdDate);

    @Query("""
            SELECT DISTINCT fi FROM FridgeItemEntity fi
            LEFT JOIN FETCH fi.product p
            LEFT JOIN FETCH p.category
            WHERE fi.fridge.id = :fridgeId
            AND p.category.id = :categoryId
            """)
    List<FridgeItemEntity> findByFridgeIdAndProductCategoryId(
            @Param("fridgeId") String fridgeId,
            @Param("categoryId") String categoryId);

    @Query("""
            SELECT DISTINCT fi FROM FridgeItemEntity fi
            LEFT JOIN FETCH fi.product p
            LEFT JOIN FETCH p.category
            WHERE fi.id = :id
            """)
    Optional<FridgeItemEntity> findWithProductAndCategoryById(@Param("id") String id);

    @Query("""
            SELECT DISTINCT fi FROM FridgeItemEntity fi
            LEFT JOIN FETCH fi.product p
            LEFT JOIN FETCH p.category
            """)
    List<FridgeItemEntity> findAllWithProductAndCategory();
}
