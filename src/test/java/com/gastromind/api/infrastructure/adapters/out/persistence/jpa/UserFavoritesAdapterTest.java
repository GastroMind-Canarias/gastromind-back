package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UserFavoritesMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserFavoritesJpaRepository;
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
class UserFavoritesAdapterTest {

    @Mock
    private UserFavoritesJpaRepository userFavoritesJpaRepository;
    @Mock
    private UserFavoritesMapper userFavoritesMapper;

    @InjectMocks
    private UserFavoritesAdapter adapter;

    @Test
    void allMethods_delegate() {
        UserFavorites domain = new UserFavorites("fav-1");
        UserFavoritesEntity entity = new UserFavoritesEntity();
        UserFavoritesEntity saved = new UserFavoritesEntity();

        when(userFavoritesMapper.toEntity(domain)).thenReturn(entity);
        when(userFavoritesJpaRepository.save(entity)).thenReturn(saved);
        when(userFavoritesMapper.toDomain(saved)).thenReturn(domain);
        assertEquals(domain, adapter.save(domain));

        when(userFavoritesJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
        when(userFavoritesJpaRepository.findById("fav-1")).thenReturn(Optional.of(entity));
        when(userFavoritesMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), adapter.findById("fav-1"));

        adapter.deleteById("fav-1");
        verify(userFavoritesJpaRepository).deleteById("fav-1");

        when(userFavoritesJpaRepository.findAll()).thenReturn(List.of(entity));
        when(userFavoritesMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), adapter.findAll());

        when(userFavoritesJpaRepository.findByUser_Id("u1")).thenReturn(List.of(entity));
        assertEquals(List.of(domain), adapter.findAllByUserId("u1"));
    }
}
