package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {

    List<TicketEntity> findByUser_Id(String userId);
}
