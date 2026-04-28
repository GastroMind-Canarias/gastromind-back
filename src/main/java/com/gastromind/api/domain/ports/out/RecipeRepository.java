package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Recipe;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para recipe.
 */
public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(String id);

    void deleteById(String id);

    List<Recipe> findAll();
}
