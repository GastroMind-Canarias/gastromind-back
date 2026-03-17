package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Product;

public interface IProductService {
    List<Product> findAll();
    Product findById(String id);
    Product create(Product product);
    Product update(String id, Product product);
    void delete(String id);
}
