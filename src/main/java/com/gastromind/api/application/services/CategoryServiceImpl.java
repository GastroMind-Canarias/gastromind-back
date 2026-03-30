package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.ports.in.ICategoryService;
import com.gastromind.api.domain.ports.out.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
    

    private final CategoryRepository repository;


    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }

    @Override
    public Category findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Categoría no encontrada"));
    }

    @Override
    public Category create(Category category) {
        return repository.save(category);
    }

    @Override
    public Category update(String id, Category category) {
        findById(id);
        category.setId(id);
        return repository.save(category);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
    
}