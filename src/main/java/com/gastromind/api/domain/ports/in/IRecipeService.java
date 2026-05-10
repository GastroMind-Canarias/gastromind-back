package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Recipe;

import java.util.List;

/**
 * Puerto de entrada para consultar recetas persistidas y metadatos asociados.
 */
public interface IRecipeService {
    List<Recipe> findAll();
    Recipe findById(String id);
    Recipe create(Recipe recipe);
    Recipe update(String id, Recipe recipe);
    void delete(String id);
}
