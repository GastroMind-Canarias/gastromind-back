package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.HouseholdMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.HouseHoldJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HouseHoldAdapterTest {

    @Mock
    private HouseHoldJpaRepository holdJpaRepository;
    @Mock
    private HouseholdMapper householdMapper;

    @InjectMocks
    private HouseHoldAdapter adapter;

    @Test
    void crudAndQueries_delegate() {
        HouseHold domain = new HouseHold("h-1");
        HouseholdEntity entity = new HouseholdEntity();
        HouseholdEntity saved = new HouseholdEntity();

        when(householdMapper.toEntity(domain)).thenReturn(entity);
        when(holdJpaRepository.save(entity)).thenReturn(saved);
        when(householdMapper.toDomain(saved)).thenReturn(domain);
        assertEquals(domain, adapter.save(domain));

        when(holdJpaRepository.existsById("h-1")).thenReturn(true);
        assertTrue(adapter.existsById("h-1"));
        when(holdJpaRepository.existsById("x")).thenReturn(false);
        assertFalse(adapter.existsById("x"));

        when(holdJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
        when(holdJpaRepository.findById("h-1")).thenReturn(Optional.of(entity));
        when(householdMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), adapter.findById("h-1"));

        adapter.deleteById("h-1");
        verify(holdJpaRepository).deleteById("h-1");

        when(holdJpaRepository.findAll()).thenReturn(List.of(entity));
        when(householdMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), adapter.findAll());
    }
}
