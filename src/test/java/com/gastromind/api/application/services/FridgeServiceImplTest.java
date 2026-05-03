package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.ports.out.FridgeRepository;
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
class FridgeServiceImplTest {

    @Mock
    private FridgeRepository repository;

    @InjectMocks
    private FridgeServiceImpl service;

    private Fridge existing;

    @BeforeEach
    void setUp() {
        HouseHold hh = new HouseHold();
        hh.setId("house-1");
        existing = new Fridge("id-1", hh);
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
        assertEquals("Nevera no encontrada", ex.getMessage());
    }

    @Test
    void create_savesAndReturns() {
        Fridge input = new Fridge();
        input.setId(null);
        Fridge saved = new Fridge("new-id", (HouseHold) null);
        when(repository.save(any(Fridge.class))).thenReturn(saved);
        assertEquals(saved, service.create(input));
        verify(repository).save(any(Fridge.class));
    }

    @Test
    void update_setsIdAndSaves() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        Fridge patch = new Fridge();
        Fridge saved = new Fridge("id-1", existing.getHouseHold_id());
        when(repository.save(any(Fridge.class))).thenReturn(saved);
        assertEquals(saved, service.update("id-1", patch));
        verify(repository).findById("id-1");
        verify(repository).save(any(Fridge.class));
    }

    @Test
    void delete_verifiesExistsThenDeletes() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).findById("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
