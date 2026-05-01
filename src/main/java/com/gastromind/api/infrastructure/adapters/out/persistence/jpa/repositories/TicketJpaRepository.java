package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Define el contrato de ticket jpa.
 */
public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {

    List<TicketEntity> findByUser_Id(String userId);

    @Query("""
            SELECT t FROM TicketEntity t JOIN t.user u LEFT JOIN u.household h
            WHERE (t.household IS NOT NULL AND t.household.id = :hid)
               OR (t.household IS NULL AND h IS NOT NULL AND h.id = :hid)
            ORDER BY t.purchaseDate DESC
            """)
    List<TicketEntity> findVisibleForHousehold(@Param("hid") String householdId);
}






