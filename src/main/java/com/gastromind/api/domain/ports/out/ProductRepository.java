package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Product;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(String id);

    void deleteById(String id);

    List<Product> findAll();
}
