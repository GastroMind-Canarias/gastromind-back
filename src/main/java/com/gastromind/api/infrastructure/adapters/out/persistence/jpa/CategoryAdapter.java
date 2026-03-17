package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.ports.out.CategoryRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.CategoryEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.CategoryJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.CategoryMapper;
@Component
public class CategoryAdapter implements CategoryRepository {

    @Autowired
    CategoryJpaRepository categoryJpaRepository;

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        return categoryMapper.toDomain(categoryJpaRepository.save(entity));
    }

    @Override
    public Optional<Category> findById(String id) {
        return categoryJpaRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public List<Category> findAll() {
        List<CategoryEntity> categoryEntities = categoryJpaRepository.findAll();
        return categoryMapper.toDomainList(categoryEntities);
    }

}
