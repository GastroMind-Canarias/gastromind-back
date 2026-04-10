package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.StoreMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.StoreJpaRepository;
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
class StoreAdapterTest {

    @Mock
    private StoreJpaRepository storeJpaRepository;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreAdapter adapter;

    @Test
    void save_and_find() {
        Store domain = new Store(null, "S");
        StoreEntity entity = new StoreEntity();
        StoreEntity saved = new StoreEntity();
        Store back = new Store("1", "S");
        when(storeMapper.toEntity(domain)).thenReturn(entity);
        when(storeJpaRepository.save(entity)).thenReturn(saved);
        when(storeMapper.toDomain(saved)).thenReturn(back);
        assertEquals(back, adapter.save(domain));
    }

    @Test
    void findFirstByNameIgnoreCase() {
        Store s = new Store("1", "Mercadona");
        when(storeJpaRepository.findFirstByNameIgnoreCase("mercadona")).thenReturn(Optional.of(new StoreEntity()));
        when(storeMapper.toDomain(org.mockito.ArgumentMatchers.any(StoreEntity.class))).thenReturn(s);
        assertEquals(Optional.of(s), adapter.findFirstByNameIgnoreCase("mercadona"));
    }

    @Test
    void findAll_and_delete() {
        when(storeJpaRepository.findAll()).thenReturn(List.of());
        when(storeMapper.toDomainList(List.of())).thenReturn(List.of());
        assertEquals(List.of(), adapter.findAll());
        adapter.deleteById("1");
        verify(storeJpaRepository).deleteById("1");
    }
}
