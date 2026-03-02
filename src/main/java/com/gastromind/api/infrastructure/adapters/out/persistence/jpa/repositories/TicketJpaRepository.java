package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;

import java.util.List;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {

    /** Devuelve los IDs de tickets de un usuario */
    @Query("SELECT t.id FROM TicketEntity t WHERE t.user.id = :userId")
    List<String> findTicketIdsByUserId(@Param("userId") String userId);

    /** Devuelve los tickets de un usuario con sus ítems cargados (para análisis) */
    @Query("SELECT DISTINCT t FROM TicketEntity t LEFT JOIN FETCH t.items ti LEFT JOIN FETCH ti.product WHERE t.user.id = :userId")
    List<TicketEntity> findByUserIdWithItems(@Param("userId") String userId);
}
