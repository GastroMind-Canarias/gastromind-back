package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Recipe;

public interface IRecipeService {
    List<Recipe> findAll();
    Recipe findById(String id);
    Recipe create(Recipe recipe);
    Recipe update(String id, Recipe recipe);
    void delete(String id);
}