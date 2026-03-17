package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.out.RecipeRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.RecipeJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.RecipeMapper;
@Component
public class RecipeAdapter implements RecipeRepository {

    @Autowired
    RecipeJpaRepository recipeJpaRepository;

    @Autowired
    RecipeMapper recipeMapper;

    @Override
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = recipeMapper.toEntity(recipe);
        return recipeMapper.toDomain(recipeJpaRepository.save(entity));
    }

    @Override
    public Optional<Recipe> findById(String id) {
        return recipeJpaRepository.findById(id).map(recipeMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        recipeJpaRepository.deleteById(id);
    }

    @Override
    public List<Recipe> findAll() {
        List<RecipeEntity> recipeEntities = recipeJpaRepository.findAll();
        return recipeMapper.toDomainList(recipeEntities);
    }

}
