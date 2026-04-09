package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Ticket;

import java.util.List;

public interface ITicketService {
    List<Ticket> findAll();
    Ticket findById(String id);
    Ticket create(Ticket ticket);
    Ticket update(String id, Ticket ticket);
    void delete(String id);
}