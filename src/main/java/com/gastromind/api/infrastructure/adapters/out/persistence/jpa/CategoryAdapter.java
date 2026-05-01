package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.ports.out.CategoryRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.CategoryEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.CategoryMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.CategoryJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa category dentro del dominio de la aplicacion.
 */
public class CategoryAdapter implements CategoryRepository {

    @Autowired
    CategoryJpaRepository categoryJpaRepository;

    @Autowired
    CategoryMapper categoryMapper;
    /**
     * Registra un nuevo category.
     * @param category la categoria
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        return categoryMapper.toDomain(categoryJpaRepository.save(entity));
    }
    /**
     * Devuelve category por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Category> findById(String id) {
        return categoryJpaRepository.findById(id).map(categoryMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        categoryJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los category.
     * @return lista actual.
     */

    @Override
    public List<Category> findAll() {
        List<CategoryEntity> categoryEntities = categoryJpaRepository.findAll();
        return categoryMapper.toDomainList(categoryEntities);
    }

}




