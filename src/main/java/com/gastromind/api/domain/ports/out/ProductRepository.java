package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Product;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para product.
 */
public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(String id);

    void deleteById(String id);

    List<Product> findAll();

    Optional<Product> findFirstByNameIgnoreCase(String name);
}
