package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UnitMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UnitJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitAdapterTest {

    @Mock
    private UnitJpaRepository unitJpaRepository;

    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitAdapter adapter;

    @Test
    void save_find_findFirst_delete() {
        Unit u = new Unit(null, "g");
        UnitEntity e = new UnitEntity();
        UnitEntity saved = new UnitEntity();
        Unit back = new Unit("1", "Gramos");
        when(unitMapper.toEntity(u)).thenReturn(e);
        when(unitJpaRepository.save(e)).thenReturn(saved);
        when(unitMapper.toDomain(saved)).thenReturn(back);
        assertEquals(back, adapter.save(u));

        when(unitJpaRepository.findById("1")).thenReturn(Optional.of(saved));
        when(unitMapper.toDomain(saved)).thenReturn(back);
        assertEquals(Optional.of(back), adapter.findById("1"));

        when(unitJpaRepository.findFirstByNameIgnoreCase("gramos")).thenReturn(Optional.of(saved));
        when(unitMapper.toDomain(saved)).thenReturn(back);
        assertEquals(Optional.of(back), adapter.findFirstByNameIgnoreCase("gramos"));

        when(unitJpaRepository.findAll()).thenReturn(List.of());
        when(unitMapper.toDomainList(List.of())).thenReturn(List.of());
        assertEquals(List.of(), adapter.findAll());

        adapter.deleteById("1");
        verify(unitJpaRepository).deleteById("1");
    }
}
