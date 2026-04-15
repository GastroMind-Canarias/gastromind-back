package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UserMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
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
class UserAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserAdapter userAdapter;

    @Test
    void save_findById_findByName_findByEmail_findByHousehold_delete_findAll() {
        User domain = new User("u-1");
        UserEntity entity = new UserEntity();
        UserEntity saved = new UserEntity();
        User mapped = new User("u-1");

        when(userMapper.toEntity(domain)).thenReturn(entity);
        when(userJpaRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDomain(saved)).thenReturn(mapped);
        assertEquals(mapped, userAdapter.save(domain));

        when(userJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(userAdapter.findById("x").isEmpty());
        when(userJpaRepository.findById("u-1")).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), userAdapter.findById("u-1"));

        when(userJpaRepository.findByName("n")).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), userAdapter.findByName("n"));

        when(userJpaRepository.findByEmail("e@x.com")).thenReturn(Optional.of(entity));
        assertEquals(Optional.of(domain), userAdapter.findByEmail("e@x.com"));

        when(userJpaRepository.findByHouseholdId("h1")).thenReturn(List.of(entity));
        when(userMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), userAdapter.findByHouseholdId("h1"));

        userAdapter.deleteById("u-1");
        verify(userJpaRepository).deleteById("u-1");

        when(userJpaRepository.findAll()).thenReturn(List.of(entity));
        when(userMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), userAdapter.findAll());
    }
}
