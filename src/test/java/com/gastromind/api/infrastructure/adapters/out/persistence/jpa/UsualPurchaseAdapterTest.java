package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UsualPurchaseMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UsualPurchaseJpaRepository;
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
class UsualPurchaseAdapterTest {

    @Mock
    private UsualPurchaseJpaRepository usualPurchaseJpaRepository;
    @Mock
    private UsualPurchaseMapper usualPurchaseMapper;

    @InjectMocks
    private UsualPurchaseAdapter adapter;

    @Test
    void allMethods_delegate() {
        UsualPurchase domain = new UsualPurchase("up-1");
        UsualPurchaseEntity entity = new UsualPurchaseEntity();
        UsualPurchaseEntity saved = new UsualPurchaseEntity();

        when(usualPurchaseMapper.toEntity(domain)).thenReturn(entity);
        when(usualPurchaseJpaRepository.save(entity)).thenReturn(saved);
        when(usualPurchaseMapper.toDomain(saved)).thenReturn(domain);
        assertEquals(domain, adapter.save(domain));

        when(usualPurchaseJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
        when(usualPurchaseJpaRepository.findById("up-1")).thenReturn(Optional.of(entity));
        when(usualPurchaseMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), adapter.findById("up-1"));

        adapter.deleteById("up-1");
        verify(usualPurchaseJpaRepository).deleteById("up-1");

        when(usualPurchaseJpaRepository.findAll()).thenReturn(List.of(entity));
        when(usualPurchaseMapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), adapter.findAll());

        when(usualPurchaseJpaRepository.findByUser_Id("u1")).thenReturn(List.of(entity));
        assertEquals(List.of(domain), adapter.findAllByUserId("u1"));

        when(usualPurchaseJpaRepository.findByUser_IdAndProduct_Id("u1", "p1")).thenReturn(Optional.of(entity));
        assertEquals(Optional.of(domain), adapter.findByUserIdAndProductId("u1", "p1"));
    }
}
