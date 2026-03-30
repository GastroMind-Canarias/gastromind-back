package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.ProductMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class ProductAdapter implements ProductRepository {

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        return productMapper.toDomain(productJpaRepository.save(entity));
    }

    @Override
    public Optional<Product> findById(String id) {
       return productJpaRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public List<Product> findAll() {
        List<ProductEntity> productEntities = productJpaRepository.findAll();
        return productMapper.toDomainList(productEntities);
    }

}
