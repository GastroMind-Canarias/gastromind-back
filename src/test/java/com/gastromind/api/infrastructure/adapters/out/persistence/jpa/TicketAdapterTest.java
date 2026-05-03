package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.TicketMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.TicketJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketAdapterTest {

    @Mock
    private TicketJpaRepository ticketJpaRepository;
    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketAdapter ticketAdapter;

    @Test
    void save_roundTrip() {
        Ticket domain = new Ticket("t-1");
        TicketEntity entity = new TicketEntity();
        TicketEntity saved = new TicketEntity();
        Ticket mapped = new Ticket("t-1");

        when(ticketMapper.toEntity(domain)).thenReturn(entity);
        when(ticketJpaRepository.save(entity)).thenReturn(saved);
        when(ticketMapper.toDomain(saved)).thenReturn(mapped);

        assertEquals(mapped, ticketAdapter.save(domain));
    }

    @Test
    void findById_emptyOrMapped() {
        when(ticketJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(ticketAdapter.findById("x").isEmpty());

        TicketEntity entity = new TicketEntity();
        Ticket domain = new Ticket("t-1");
        when(ticketJpaRepository.findById("t-1")).thenReturn(Optional.of(entity));
        when(ticketMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), ticketAdapter.findById("t-1"));
    }

    @Test
    void deleteById_delegates() {
        ticketAdapter.deleteById("t-1");
        verify(ticketJpaRepository).deleteById("t-1");
    }

    @Test
    void findAll_mapsList() {
        List<TicketEntity> entities = List.of(new TicketEntity());
        List<Ticket> domains = List.of(new Ticket("t-1"));
        when(ticketJpaRepository.findAll()).thenReturn(entities);
        when(ticketMapper.toDomainList(entities)).thenReturn(domains);
        assertEquals(domains, ticketAdapter.findAll());
    }
}
