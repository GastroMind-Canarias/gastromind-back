package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.ports.out.UserFavoritesRepository;
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
class UserFavoritesServiceImplTest {

    @Mock
    private UserFavoritesRepository repository;

    @InjectMocks
    private UserFavoritesServiceImpl service;

    private UserFavorites existing;

    @BeforeEach
    void setUp() {
        existing = new UserFavorites("id-1", null, null);
    }

    @Test
    void crud_flow() {
        when(repository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());

        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findById("id-1"));

        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("missing"));

        UserFavorites in = new UserFavorites();
        when(repository.save(in)).thenReturn(existing);
        assertEquals(existing, service.create(in));

        UserFavorites patch = new UserFavorites();
        when(repository.save(any(UserFavorites.class))).thenReturn(existing);
        assertEquals(existing, service.update("id-1", patch));

        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.delete("id-1");
        verify(repository).deleteById(eq("id-1"));
    }
}
