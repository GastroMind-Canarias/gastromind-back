package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.out.RecipeRepository;
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
class RecipeServiceImplTest {

    @Mock
    private RecipeRepository repository;

    @InjectMocks
    private RecipeServiceImpl service;

    private Recipe existing;

    @BeforeEach
    void setUp() {
        existing = new Recipe("id-1");
        existing.setTitle("Tarta");
    }

    @Test
    void findAll_delegates() {
        when(repository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());
    }

    @Test
    void findById_andCrud() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findById("id-1"));

        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("missing"));

        Recipe created = new Recipe();
        when(repository.save(created)).thenReturn(existing);
        assertEquals(existing, service.create(created));

        Recipe patch = new Recipe();
        when(repository.save(any(Recipe.class))).thenReturn(existing);
        assertEquals(existing, service.update("id-1", patch));

        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
