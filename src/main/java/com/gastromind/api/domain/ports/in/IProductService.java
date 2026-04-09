package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Product;

import java.util.List;

public interface IProductService {
    List<Product> findAll();
    Product findById(String id);
    Product create(Product product);
    Product update(String id, Product product);
    void delete(String id);
}
