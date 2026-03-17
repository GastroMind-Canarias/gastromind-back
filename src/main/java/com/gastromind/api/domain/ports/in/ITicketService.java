package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Ticket;

public interface ITicketService {
    List<Ticket> findAll();
    Ticket findById(String id);
    Ticket create(Ticket ticket);
    Ticket update(String id, Ticket ticket);
    void delete(String id);
}