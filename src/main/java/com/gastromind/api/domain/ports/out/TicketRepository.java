package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(String id);

    void deleteById(String id);

    List<Ticket> findAll();

    List<Ticket> findAllByUserId(String userId);
}
