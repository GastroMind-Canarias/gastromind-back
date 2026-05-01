package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.ports.out.TicketRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.TicketMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.TicketJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa ticket dentro del dominio de la aplicacion.
 */
public class TicketAdapter implements TicketRepository {

    @Autowired
    TicketJpaRepository ticketJpaRepository;

    @Autowired
    TicketMapper ticketMapper;
    /**
     * Registra un nuevo ticket.
     * @param ticket el ticket
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = ticketMapper.toEntity(ticket);
        return ticketMapper.toDomain(ticketJpaRepository.save(entity));
    }
    /**
     * Devuelve ticket por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Ticket> findById(String id) {
        return ticketJpaRepository.findById(id).map(ticketMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
       ticketJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los ticket.
     * @return lista actual.
     */

    @Override
    public List<Ticket> findAll() {
        List<TicketEntity> ticketEntities = ticketJpaRepository.findAll();
        return ticketMapper.toDomainList(ticketEntities);
    }
    /**
     * Realiza find all by user id.
     * @param userId el identificador del usuario
     * @return lista actual.
     */

    @Override
    public List<Ticket> findAllByUserId(String userId) {
        return ticketMapper.toDomainList(ticketJpaRepository.findByUser_Id(userId));
    }
    /**
     * Realiza find visible for household.
     * @param householdId el identificador del hogar
     * @return lista actual.
     */

    @Override
    public List<Ticket> findVisibleForHousehold(String householdId) {
        return ticketMapper.toDomainList(ticketJpaRepository.findVisibleForHousehold(householdId));
    }
}




