package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.ports.in.ITicketService;
import com.gastromind.api.domain.ports.out.TicketRepository;

@Service
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository repository;


    public TicketServiceImpl(TicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Ticket> findAll() {
        return repository.findAll();
    }

    @Override
    public Ticket findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Ticket no encontrado"));
    }

    @Override
    public Ticket create(Ticket ticket) {
        return repository.save(ticket);
    }

    @Override
    public Ticket update(String id, Ticket ticket) {
        findById(id);
        ticket.setId(id);
        return repository.save(ticket);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}