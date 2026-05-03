package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Category;

import java.util.List;

/**
 * Define las operaciones de negocio para categorias.
 */
public interface ICategoryService {
    List<Category> findAll();
    Category findById(String id);
    Category create(Category category);
    Category update(String id, Category category);
    void delete(String id);
}
