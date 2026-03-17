package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;

import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {
    
}
