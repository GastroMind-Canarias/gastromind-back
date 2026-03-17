package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Category;

public interface ICategoryService {
    List<Category> findAll();
    Category findById(String id);
    Category create(Category category);
    Category update(String id, Category category);
    void delete(String id);
}
