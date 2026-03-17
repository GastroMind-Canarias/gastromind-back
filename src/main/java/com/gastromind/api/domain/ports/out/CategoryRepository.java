package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Category;

public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findById(String id);

    void deleteById(String id);

    List<Category> findAll();
}
