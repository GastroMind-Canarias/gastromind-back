package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Category;

import java.util.List;
import java.util.Optional;

/**
 * Categorías jerárquicas o planas del catálogo de productos/recetas.
 */
public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findById(String id);

    void deleteById(String id);

    List<Category> findAll();
}
