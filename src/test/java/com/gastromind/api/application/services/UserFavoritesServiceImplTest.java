package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.User;
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
import static org.junit.jupiter.api.Assertions.assertSame;
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
        existing = new UserFavorites("id-1", new User("user-1"), null);
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

    @Test
    void findAllByUserId_delegates() {
        when(repository.findAllByUserId("user-1")).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAllByUserId("user-1"));
    }

    @Test
    void findByIdForUser_returnsWhenOwner() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        assertSame(existing, service.findByIdForUser("id-1", "user-1"));
    }

    @Test
    void findByIdForUser_throwsWhenNotOwner() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        assertThrows(ForbiddenException.class, () -> service.findByIdForUser("id-1", "other"));
    }

    @Test
    void updateForUser_keepsOwner() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        UserFavorites patch = new UserFavorites();
        when(repository.save(any(UserFavorites.class))).thenAnswer(inv -> inv.getArgument(0));
        UserFavorites out = service.updateForUser("id-1", patch, "user-1");
        assertEquals("user-1", out.getUser_id().getId());
    }

    @Test
    void deleteForUser_verifiesOwner() {
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        service.deleteForUser("id-1", "user-1");
        verify(repository).deleteById(eq("id-1"));
    }
}

