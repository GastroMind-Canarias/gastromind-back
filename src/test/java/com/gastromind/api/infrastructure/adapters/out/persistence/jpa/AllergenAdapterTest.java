package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.AllergenMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.AllergenJpaRepository;
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
class AllergenAdapterTest {

    @Mock
    private AllergenJpaRepository allergenJpaRepository;

    @Mock
    private AllergenMapper allergenMapper;

    @InjectMocks
    private AllergenAdapter allergenAdapter;

    @Test
    void save_mapsToEntity_savesAndMapsBack() {
        Allergen domain = new Allergen(null, "Gluten");
        AllergenEntity entity = new AllergenEntity();
        AllergenEntity savedEntity = new AllergenEntity("a-1", "Gluten", null, null);
        Allergen returned = new Allergen("a-1", "Gluten");

        when(allergenMapper.toEntity(domain)).thenReturn(entity);
        when(allergenJpaRepository.save(entity)).thenReturn(savedEntity);
        when(allergenMapper.toDomain(savedEntity)).thenReturn(returned);

        assertEquals(returned, allergenAdapter.save(domain));
        verify(allergenJpaRepository).save(entity);
    }

    @Test
    void findById_returnsEmptyWhenMissing() {
        when(allergenJpaRepository.findById("x")).thenReturn(Optional.empty());

        assertTrue(allergenAdapter.findById("x").isEmpty());
    }

    @Test
    void findById_mapsWhenPresent() {
        AllergenEntity entity = new AllergenEntity("a-1", "Gluten", null, null);
        Allergen domain = new Allergen("a-1", "Gluten");
        when(allergenJpaRepository.findById("a-1")).thenReturn(Optional.of(entity));
        when(allergenMapper.toDomain(entity)).thenReturn(domain);

        assertEquals(Optional.of(domain), allergenAdapter.findById("a-1"));
    }

    @Test
    void deleteById_delegatesToJpa() {
        allergenAdapter.deleteById("a-1");
        verify(allergenJpaRepository).deleteById("a-1");
    }

    @Test
    void findAll_mapsEntityListToDomain() {
        List<AllergenEntity> entities = List.of(new AllergenEntity("a-1", "Gluten", null, null));
        List<Allergen> domains = List.of(new Allergen("a-1", "Gluten"));
        when(allergenJpaRepository.findAll()).thenReturn(entities);
        when(allergenMapper.toDomainList(entities)).thenReturn(domains);

        assertEquals(domains, allergenAdapter.findAll());
    }
}
