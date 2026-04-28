package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.out.RecipeRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.RecipeMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.RecipeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa recipe dentro del dominio de la aplicacion.
 */
public class RecipeAdapter implements RecipeRepository {

    @Autowired
    RecipeJpaRepository recipeJpaRepository;

    @Autowired
    RecipeMapper recipeMapper;
    /**
     * Registra un nuevo recipe.
     * @param recipe la receta
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = recipeMapper.toEntity(recipe);
        return recipeMapper.toDomain(recipeJpaRepository.save(entity));
    }
    /**
     * Devuelve recipe por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Recipe> findById(String id) {
        return recipeJpaRepository.findById(id).map(recipeMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        recipeJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los recipe.
     * @return lista actual.
     */

    @Override
    public List<Recipe> findAll() {
        List<RecipeEntity> recipeEntities = recipeJpaRepository.findAll();
        return recipeMapper.toDomainList(recipeEntities);
    }

}




