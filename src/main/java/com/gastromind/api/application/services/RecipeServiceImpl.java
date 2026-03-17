package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.in.IRecipeService;
import com.gastromind.api.domain.ports.out.RecipeRepository;

@Service
public class RecipeServiceImpl implements IRecipeService {

    private final RecipeRepository repository;


    public RecipeServiceImpl(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Recipe> findAll() {
        return repository.findAll();
    }

    @Override
    public Recipe findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Receta no encontrada"));
    }

    @Override
    public Recipe create(Recipe recipe) {
        return repository.save(recipe);
    }

    @Override
    public Recipe update(String id, Recipe recipe) {
        findById(id);
        recipe.setId(id);
        return repository.save(recipe);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}