package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Ticket;

import java.util.List;

public interface ITicketService {
    List<Ticket> findAll();

    List<Ticket> findAllByUserId(String userId);

    Ticket findById(String id);

    Ticket findByIdForUser(String ticketId, String userId);

    Ticket create(Ticket ticket);

    Ticket update(String id, Ticket ticket);

    Ticket updateForUser(String id, Ticket ticket, String userId);

    void delete(String id);

    void deleteForUser(String id, String userId);
}