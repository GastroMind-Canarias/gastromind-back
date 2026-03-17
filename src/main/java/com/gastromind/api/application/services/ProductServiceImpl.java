package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.in.IProductService;
import com.gastromind.api.domain.ports.out.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository repository;


    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public Product findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Producto no encontrado"));
    }

    @Override
    public Product create(Product product) {
        return repository.save(product);
    }

    @Override
    public Product update(String id, Product product) {
        findById(id);
        product.setId(id);
        return repository.save(product);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}