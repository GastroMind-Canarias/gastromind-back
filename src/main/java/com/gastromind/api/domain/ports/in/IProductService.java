package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Product;

import java.util.List;

/**
 * Define las operaciones de negocio para productos.
 */
public interface IProductService {
    List<Product> findAll();
    Product findById(String id);
    Product create(Product product);
    List<Product> createBatch(List<String> names);
    Product update(String id, Product product);
    void delete(String id);
}
