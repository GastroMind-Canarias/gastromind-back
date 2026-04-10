package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.FridgeMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.FridgeJpaRepository;
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
class FridgeAdapterTest {

    @Mock
    private FridgeJpaRepository fridgeJpaRepository;
    @Mock
    private FridgeMapper fridgeMapper;

    @InjectMocks
    private FridgeAdapter fridgeAdapter;

    @Test
    void save_roundTrip() {
        Fridge domain = new Fridge("f-1");
        FridgeEntity entity = new FridgeEntity();
        FridgeEntity saved = new FridgeEntity();
        Fridge mapped = new Fridge("f-1");

        when(fridgeMapper.toEntity(domain)).thenReturn(entity);
        when(fridgeJpaRepository.save(entity)).thenReturn(saved);
        when(fridgeMapper.toDomain(saved)).thenReturn(mapped);

        assertEquals(mapped, fridgeAdapter.save(domain));
    }

    @Test
    void findById_and_findAll() {
        when(fridgeJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(fridgeAdapter.findById("x").isEmpty());

        FridgeEntity entity = new FridgeEntity();
        Fridge domain = new Fridge("f-1");
        when(fridgeJpaRepository.findById("f-1")).thenReturn(Optional.of(entity));
        when(fridgeMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), fridgeAdapter.findById("f-1"));

        List<FridgeEntity> entities = List.of(entity);
        List<Fridge> domains = List.of(domain);
        when(fridgeJpaRepository.findAll()).thenReturn(entities);
        when(fridgeMapper.toDomainList(entities)).thenReturn(domains);
        assertEquals(domains, fridgeAdapter.findAll());
    }

    @Test
    void findByHouseholdId_mapsList() {
        List<FridgeEntity> entities = List.of(new FridgeEntity());
        List<Fridge> domains = List.of(new Fridge("f-1"));
        when(fridgeJpaRepository.findByHousehold_Id("h-1")).thenReturn(entities);
        when(fridgeMapper.toDomainList(entities)).thenReturn(domains);
        assertEquals(domains, fridgeAdapter.findByHouseholdId("h-1"));
    }

    @Test
    void deleteById_delegates() {
        fridgeAdapter.deleteById("f-1");
        verify(fridgeJpaRepository).deleteById("f-1");
    }
}
