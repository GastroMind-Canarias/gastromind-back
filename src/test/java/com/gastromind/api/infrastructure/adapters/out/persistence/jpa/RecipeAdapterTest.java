package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.RecipeMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.RecipeJpaRepository;
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
class RecipeAdapterTest {

    @Mock
    private RecipeJpaRepository recipeJpaRepository;
    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeAdapter adapter;

    @Test
    void allMethods_delegate() {
        Recipe domain = new Recipe("r-1");
        RecipeEntity entity = new RecipeEntity();
        RecipeEntity saved = new RecipeEntity();

        when(recipeMapper.toEntity(domain)).thenReturn(entity);
        when(recipeJpaRepository.save(entity)).thenReturn(saved);
        when(recipeMapper.toDomain(saved)).thenReturn(domain);
        assertEquals(domain, adapter.save(domain));

        when(recipeJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
        when(recipeJpaRepository.findById("r-1")).thenReturn(Optional.of(entity));
        when(recipeMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), adapter.findById("r-1"));

        adapter.deleteById("r-1");
        verify(recipeJpaRepository).deleteById("r-1");

        when(recipeJpaRepository.findAll()).thenReturn(List.of(entity));
        when(recipeMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), adapter.findAll());
    }
}
