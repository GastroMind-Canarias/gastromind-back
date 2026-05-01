package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.ports.in.IRecipeService;
import com.gastromind.api.domain.ports.out.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar recetas.
 */
public class RecipeServiceImpl implements IRecipeService {

    private final RecipeRepository repository;
    /**
     * Crea el servicio con el repositorio de recetas.
     * @param repository repositorio de persistencia de recetas
     */


    public RecipeServiceImpl(RecipeRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todas las recetas registradas.
     * @return listado completo de recetas
     */

    @Override
    public List<Recipe> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una receta por su identificador.
     * @param id identificador de la receta
     * @return receta encontrada
     * @throws NotFoundException si no existe una receta con ese id
     */

    @Override
    public Recipe findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Receta no encontrada"));
    }
    /**
     * Crea una nueva receta.
     * @param recipe datos de la receta a crear
     * @return receta persistida
     */

    @Override
    public Recipe create(Recipe recipe) {
        return repository.save(recipe);
    }
    /**
     * Define una receta existente.
     * @param id identificador de la receta a actualizar
     * @param recipe nuevos datos de la receta
     * @return receta actualizada
     * @throws NotFoundException si no existe una receta con ese id
     */

    @Override
    public Recipe update(String id, Recipe recipe) {
        findById(id);
        recipe.setId(id);
        return repository.save(recipe);
    }
    /**
     * Elimina una receta por su identificador.
     * @param id identificador de la receta a eliminar
     * @throws NotFoundException si no existe una receta con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}




