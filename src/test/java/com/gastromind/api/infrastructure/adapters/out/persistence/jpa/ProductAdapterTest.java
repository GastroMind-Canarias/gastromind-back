package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.ProductMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.ProductJpaRepository;
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
class ProductAdapterTest {

    @Mock
    private ProductJpaRepository productJpaRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductAdapter productAdapter;

    @Test
    void save_mapsAndDelegates() {
        Product domain = new Product("p-1");
        ProductEntity entity = new ProductEntity();
        ProductEntity saved = new ProductEntity();
        Product mapped = new Product("p-1");

        when(productMapper.toEntity(domain)).thenReturn(entity);
        when(productJpaRepository.save(entity)).thenReturn(saved);
        when(productMapper.toDomain(saved)).thenReturn(mapped);

        assertEquals(mapped, productAdapter.save(domain));
        verify(productJpaRepository).save(entity);
    }

    @Test
    void findById_emptyOrMapped() {
        when(productJpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(productAdapter.findById("x").isEmpty());

        ProductEntity entity = new ProductEntity();
        Product domain = new Product("p-1");
        when(productJpaRepository.findById("p-1")).thenReturn(Optional.of(entity));
        when(productMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), productAdapter.findById("p-1"));
    }

    @Test
    void deleteById_delegates() {
        productAdapter.deleteById("p-1");
        verify(productJpaRepository).deleteById("p-1");
    }

    @Test
    void findAll_mapsList() {
        List<ProductEntity> entities = List.of(new ProductEntity());
        List<Product> domains = List.of(new Product("p-1"));
        when(productJpaRepository.findAll()).thenReturn(entities);
        when(productMapper.toDomainList(entities)).thenReturn(domains);
        assertEquals(domains, productAdapter.findAll());
    }

    @Test
    void findFirstByNameIgnoreCase_mapsOptional() {
        when(productJpaRepository.findFirstByNameIgnoreCase("milk")).thenReturn(Optional.empty());
        assertTrue(productAdapter.findFirstByNameIgnoreCase("milk").isEmpty());

        ProductEntity entity = new ProductEntity();
        Product domain = new Product("p-1");
        when(productJpaRepository.findFirstByNameIgnoreCase("milk")).thenReturn(Optional.of(entity));
        when(productMapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), productAdapter.findFirstByNameIgnoreCase("milk"));
    }
}
