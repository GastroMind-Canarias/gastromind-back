package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.FridgeItemMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.FridgeItemJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FridgeItemAdapterTest {

    @Mock
    private FridgeItemJpaRepository fridgeItemJpaRepository;
    @Mock
    private FridgeItemMapper fridgeItemMapper;

    @InjectMocks
    private FridgeItemAdapter adapter;

    @Test
    void allMethods_delegate() {
        FridgeItem domain = new FridgeItem();
        domain.setId("fi-1");
        FridgeItemEntity entity = new FridgeItemEntity();
        FridgeItemEntity saved = new FridgeItemEntity();

        when(fridgeItemMapper.toEntity(domain)).thenReturn(entity);
        when(fridgeItemJpaRepository.save(entity)).thenReturn(saved);
        when(fridgeItemMapper.toDomain(saved)).thenReturn(domain);
        assertEquals(domain, adapter.save(domain));

        when(fridgeItemJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
        when(fridgeItemJpaRepository.findById("fi-1")).thenReturn(Optional.of(entity));
        when(fridgeItemMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), adapter.findById("fi-1"));

        when(fridgeItemJpaRepository.findByFridgeId("f1")).thenReturn(List.of(entity));
        when(fridgeItemMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), adapter.findByFridgeId("f1"));

        LocalDate d = LocalDate.now();
        when(fridgeItemJpaRepository.findByFridgeIdAndExpirationDateBefore("f1", d)).thenReturn(List.of(entity));
        assertEquals(List.of(domain), adapter.findExpiringItems("f1", d));

        when(fridgeItemJpaRepository.findByFridgeIdAndProductCategoryId("f1", "c1")).thenReturn(List.of(entity));
        assertEquals(List.of(domain), adapter.findByFridgeIdAndCategoryId("f1", "c1"));

        when(fridgeItemJpaRepository.findAll()).thenReturn(List.of(entity));
        assertEquals(List.of(domain), adapter.findAll());

        adapter.deleteById("fi-1");
        verify(fridgeItemJpaRepository).deleteById("fi-1");
    }
}
