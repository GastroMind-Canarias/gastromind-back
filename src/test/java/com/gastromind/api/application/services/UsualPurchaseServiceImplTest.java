package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsualPurchaseServiceImplTest {

    @Mock
    private UsualPurchaseRepository repository;

    @InjectMocks
    private UsualPurchaseServiceImpl service;

    private UsualPurchase existing;

    @BeforeEach
    void setUp() {
        existing = new UsualPurchase("id-1", null, null, 1f);
    }

    @Test
    void crud_flow() {
        when(repository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());

        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findById("id-1"));

        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("missing"));

        UsualPurchase in = new UsualPurchase();
        when(repository.save(in)).thenReturn(existing);
        assertEquals(existing, service.create(in));

        UsualPurchase patch = new UsualPurchase();
        when(repository.save(any(UsualPurchase.class))).thenReturn(existing);
        assertEquals(existing, service.update("id-1", patch));

        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
