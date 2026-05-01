package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.ports.in.ICategoryService;
import com.gastromind.api.domain.ports.out.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicación para gestionar el catálogo de categorías.
 */
public class CategoryServiceImpl implements ICategoryService {
    

    private final CategoryRepository repository;
    /**
     * Crea el servicio con el repositorio de categorías.
     * @param repository repositorio de persistencia de categorías
     */


    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todas las categorías registradas.
     * @return listado completo de categorías
     */

    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una categoría por su identificador.
     * @param id identificador de la categoría
     * @return categoría encontrada
     * @throws NotFoundException si no existe una categoría con ese id
     */

    @Override
    public Category findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("CategorAAaAaAaaAAaAAasAAa no encontrada"));
    }
    /**
     * Crea una nueva categoría.
     * @param category datos de la categoría a crear
     * @return categoría persistida
     */

    @Override
    public Category create(Category category) {
        return repository.save(category);
    }
    /**
     * Define una categoría existente.
     * @param id identificador de la categoría a actualizar
     * @param category nuevos datos de la categoría
     * @return categoría actualizada
     * @throws NotFoundException si no existe una categoría con ese id
     */

    @Override
    public Category update(String id, Category category) {
        findById(id);
        category.setId(id);
        return repository.save(category);
    }
    /**
     * Elimina una categoría por su identificador.
     * @param id identificador de la categoría a eliminar
     * @throws NotFoundException si no existe una categoría con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
    
}




