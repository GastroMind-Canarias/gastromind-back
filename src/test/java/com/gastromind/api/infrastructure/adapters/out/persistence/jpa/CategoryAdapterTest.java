package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.CategoryEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.CategoryMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.CategoryJpaRepository;
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
class CategoryAdapterTest {

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryAdapter adapter;

    @Test
    void save_roundTripsThroughMapperAndJpa() {
        Category domain = new Category(null, "Cat");
        CategoryEntity entity = new CategoryEntity();
        CategoryEntity saved = new CategoryEntity();
        saved.setId("c1");
        Category back = new Category("c1", "Cat");
        when(categoryMapper.toEntity(domain)).thenReturn(entity);
        when(categoryJpaRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toDomain(saved)).thenReturn(back);
        assertEquals(back, adapter.save(domain));
    }

    @Test
    void findById_empty() {
        when(categoryJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
    }

    @Test
    void findAll_mapsList() {
        List<CategoryEntity> entities = List.of();
        when(categoryJpaRepository.findAll()).thenReturn(entities);
        when(categoryMapper.toDomainList(entities)).thenReturn(List.of());
        assertEquals(List.of(), adapter.findAll());
    }

    @Test
    void deleteById_delegates() {
        adapter.deleteById("c1");
        verify(categoryJpaRepository).deleteById("c1");
    }
}
