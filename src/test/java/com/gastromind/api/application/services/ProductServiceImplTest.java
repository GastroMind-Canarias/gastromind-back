package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product existing;

    @BeforeEach
    void setUp() {
        existing = new Product("id-1", "Leche", false, null);
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
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void create_savesAndReturns() {
        Product input = new Product(null, "Nuevo", false, null);
        Product saved = new Product("new-id", "Nuevo", false, null);
        when(repository.save(input)).thenReturn(saved);
        assertEquals(saved, service.create(input));
        verify(repository).save(input);
    }

    @Test
    void update_setsIdAndSaves() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        Product patch = new Product(null, "Actualizado", false, null);
        Product saved = new Product("id-1", "Actualizado", false, null);
        when(repository.save(any(Product.class))).thenReturn(saved);
        assertEquals(saved, service.update("id-1", patch));
        verify(repository).findById("id-1");
        verify(repository).save(any(Product.class));
    }

    @Test
    void delete_verifiesExistsThenDeletes() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).findById("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
