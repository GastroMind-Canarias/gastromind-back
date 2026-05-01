package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
/**
 * Define el contrato de ticket item jpa.
 */
public interface TicketItemJpaRepository extends JpaRepository<TicketItemEntity, String> {

    @Query("""
            SELECT ti FROM TicketItemEntity ti
            JOIN FETCH ti.ticket t
            JOIN FETCH ti.unit u
            JOIN FETCH ti.product p
            JOIN t.user tu
            LEFT JOIN tu.household th
            WHERE t.purchaseDate >= :since
            AND (
              (t.household IS NOT NULL AND t.household.id = :householdId)
              OR (t.household IS NULL AND th IS NOT NULL AND th.id = :householdId)
            )
            """)
    List<TicketItemEntity> findForHouseholdSince(
            @Param("householdId") String householdId,
            @Param("since") LocalDateTime since);
}






