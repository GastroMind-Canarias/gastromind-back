package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.out.UnitRepository;
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
class UnitServiceImplTest {

    @Mock
    private UnitRepository repository;

    @InjectMocks
    private UnitServiceImpl service;

    private Unit existing;

    @BeforeEach
    void setUp() {
        existing = new Unit("id-1", "Kilogramos");
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
        assertEquals("Unidad de Medida no encontrada", ex.getMessage());
    }

    @Test
    void create_savesAndReturns() {
        Unit input = new Unit(null, "Nuevo");
        Unit saved = new Unit("new-id", "Nuevo");
        when(repository.save(input)).thenReturn(saved);
        assertEquals(saved, service.create(input));
        verify(repository).save(input);
    }

    @Test
    void update_setsIdAndSaves() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        Unit patch = new Unit(null, "Actualizado");
        Unit saved = new Unit("id-1", "Actualizado");
        when(repository.save(any(Unit.class))).thenReturn(saved);
        assertEquals(saved, service.update("id-1", patch));
        verify(repository).findById("id-1");
        verify(repository).save(any(Unit.class));
    }

    @Test
    void delete_verifiesExistsThenDeletes() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).findById("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
