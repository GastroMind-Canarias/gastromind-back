package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Recipe;

import java.util.List;

/**
 * Define las operaciones de negocio para recetas.
 */
public interface IRecipeService {
    List<Recipe> findAll();
    Recipe findById(String id);
    Recipe create(Recipe recipe);
    Recipe update(String id, Recipe recipe);
    void delete(String id);
}
