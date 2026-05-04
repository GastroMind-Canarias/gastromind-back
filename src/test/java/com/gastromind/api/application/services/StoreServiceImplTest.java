package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.ports.out.AliasRateLimitPort;
import com.gastromind.api.domain.ports.out.StoreAliasRepository;
import com.gastromind.api.domain.ports.out.StoreRepository;
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
class StoreServiceImplTest {

    @Mock
    private StoreRepository repository;
    @Mock
    private StoreAliasRepository storeAliasRepository;
    @Mock
    private AliasRateLimitPort aliasRateLimitPort;
    @Mock
    private StoreNameNormalizer normalizer;
    @Mock
    private PendingStoreService pendingStoreService;

    @InjectMocks
    private StoreServiceImpl service;

    private Store existing;

    @BeforeEach
    void setUp() {
        existing = new Store("id-1", "Mercadona");
    }

    @Test
    void findAll_delegatesToRepository() {
        when(repository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());
        verify(repository).findAll();
    }

    @Test
    void findById_returnsWhenPresent() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findById("id-1"));
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.findById("missing"));
        assertEquals("Tienda no encontrada", ex.getMessage());
    }

    @Test
    void create_savesAndReturns() {
        Store input = new Store(null, "Nuevo");
        Store saved = new Store("new-id", "Nuevo");
        when(normalizer.normalize("Nuevo")).thenReturn("nuevo");
        when(repository.save(any(Store.class))).thenReturn(saved);
        assertEquals(saved, service.create(input));
        verify(repository).save(any(Store.class));
    }

    @Test
    void update_setsIdAndSaves() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        Store patch = new Store(null, "Actualizado");
        Store saved = new Store("id-1", "Actualizado");
        when(normalizer.normalize("Actualizado")).thenReturn("actualizado");
        when(repository.save(any(Store.class))).thenReturn(saved);
        assertEquals(saved, service.update("id-1", patch));
        verify(repository).findById("id-1");
        verify(repository).save(any(Store.class));
    }

    @Test
    void delete_verifiesExistsThenDeletes() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).findById("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
