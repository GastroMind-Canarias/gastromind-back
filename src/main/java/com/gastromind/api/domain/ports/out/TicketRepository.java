package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Ticket;

public interface TicketRepository {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(String id);

    void deleteById(String id);

    List<Ticket> findAll();
}
