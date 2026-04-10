package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.config.UsualPurchaseProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsualPurchaseTicketSyncServiceTest {

    @Mock
    private UsualPurchaseRepository repository;

    private UsualPurchaseTicketSyncService service;

    @BeforeEach
    void setUp() {
        UsualPurchaseProperties props = new UsualPurchaseProperties();
        props.setTicketSyncBlendWeight(0.5);
        service = new UsualPurchaseTicketSyncService(repository, props);
    }

    @Test
    void createsRowWhenAbsent() {
        when(repository.findByUserIdAndProductId("u1", "p1")).thenReturn(Optional.empty());
        when(repository.save(any(UsualPurchase.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket t = new Ticket();
        t.setUser_id(new User("u1"));
        TicketItem line = new TicketItem();
        line.setProduct(new Product("p1"));
        line.setQuantity(BigDecimal.ONE);
        line.setUnit(new Unit("u", "Unidades"));
        t.setItems(List.of(line));

        service.syncAfterTicketCreated(t);

        ArgumentCaptor<UsualPurchase> cap = ArgumentCaptor.forClass(UsualPurchase.class);
        verify(repository).save(cap.capture());
        assertEquals(1f, cap.getValue().getTarget_quantity());
        assertEquals("u1", cap.getValue().getUser_id().getId());
    }

    @Test
    void blendsWhenPresent() {
        UsualPurchase existing = new UsualPurchase("id", new User("u1"), new Product("p1"), 2f);
        when(repository.findByUserIdAndProductId("u1", "p1")).thenReturn(Optional.of(existing));
        when(repository.save(any(UsualPurchase.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket t = new Ticket();
        t.setUser_id(new User("u1"));
        TicketItem line = new TicketItem();
        line.setProduct(new Product("p1"));
        line.setQuantity(BigDecimal.ONE);
        line.setUnit(new Unit("u", "Unidades"));
        t.setItems(List.of(line));

        service.syncAfterTicketCreated(t);

        ArgumentCaptor<UsualPurchase> cap = ArgumentCaptor.forClass(UsualPurchase.class);
        verify(repository).save(cap.capture());
        assertTrue(cap.getValue().getTarget_quantity() > 1f && cap.getValue().getTarget_quantity() < 2f);
    }

    @Test
    void noOpWhenNoItems() {
        Ticket t = new Ticket();
        t.setUser_id(new User("u1"));
        t.setItems(List.of());
        service.syncAfterTicketCreated(t);
        verify(repository, org.mockito.Mockito.never()).save(any());
    }
}
