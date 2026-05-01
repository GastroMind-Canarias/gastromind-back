package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.ports.in.ICategoryService;
import com.gastromind.api.domain.ports.out.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar el catalogo de categorias.
 */
public class CategoryServiceImpl implements ICategoryService {
    

    private final CategoryRepository repository;
    /**
     * Crea el servicio con el repositorio de categorias.
     * @param repository repositorio de persistencia de categorias
     */


    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todas las categorias registradas.
     * @return listado completo de categorias
     */

    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una categoria por su identificador.
     * @param id identificador de la categoria
     * @return categoria encontrada
     * @throws NotFoundException si no existe una categoria con ese id
     */

    @Override
    public Category findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Categoria no encontrada"));
    }
    /**
     * Crea una nueva categoria.
     * @param category datos de la categoria a crear
     * @return categoria persistida
     */

    @Override
    public Category create(Category category) {
        return repository.save(category);
    }
    /**
     * Define una categoria existente.
     * @param id identificador de la categoria a actualizar
     * @param category nuevos datos de la categoria
     * @return categoria actualizada
     * @throws NotFoundException si no existe una categoria con ese id
     */

    @Override
    public Category update(String id, Category category) {
        findById(id);
        category.setId(id);
        return repository.save(category);
    }
    /**
     * Elimina una categoria por su identificador.
     * @param id identificador de la categoria a eliminar
     * @throws NotFoundException si no existe una categoria con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
    
}




