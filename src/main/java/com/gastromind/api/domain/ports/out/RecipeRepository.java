package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Recipe;

import java.util.List;
import java.util.Optional;

/**
 * Recetas guardadas y sus metadatos para consulta desde la API y favoritos.
 */
public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(String id);

    void deleteById(String id);

    List<Recipe> findAll();
}
