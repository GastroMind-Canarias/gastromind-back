package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.CategoryServiceImpl;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.CategoryRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categoría", description = "Gestión del catálogo de categorías para productos y recetas.")
/**
 * Controlador REST para gestionar categorías.
 */
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private CategoryRestMapper categoryMapper;
    /**
     * Lista todas las categorías.
     *
     * @return colección de categorías
     */

    @Operation(summary = "Obtener todas las categorías", description = "Devuelve la lista completa de categorías registradas.")
    @GetMapping
    @ApiStandardDoc
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<Category> categories = categoryServiceImpl.findAll();
        return ResponseEntity.ok(categoryMapper.toResponseList(categories));
    }
    /**
     * Recupera una categoría por ID.
     *
     * @param id identificador de la categoría
     * @return categoría encontrada
     */

    @Operation(summary = "Buscar categoría por ID", description = "Devuelve una categoría concreta a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @Parameter(description = "ID de la categoría a buscar", example = "1") @PathVariable String id) {
        Category category = categoryServiceImpl.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }
    /**
     * Crea una categoría.
     *
     * @param categoryRequest datos de alta
     * @return categoría creada
     */

    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category categoryDomain = categoryMapper.toDomain(categoryRequest);
        Category savedCategory = categoryServiceImpl.create(categoryDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(savedCategory));
    }
    /**
     * Define una categoría existente.
     *
     * @param id identificador de la categoría
     * @param categoryRequest datos actualizados
     * @return categoría actualizada
     */

    @Operation(summary = "Actualizar categoría", description = "Modifica los datos de una categoría existente.")
    @PutMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<CategoryResponse> update(@PathVariable String id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category categoryDomain = categoryMapper.toDomain(categoryRequest);
        Category updatedCategory = categoryServiceImpl.update(id, categoryDomain);
        return ResponseEntity.ok(categoryMapper.toResponse(updatedCategory));
    }
    /**
     * Elimina una categoría.
     *
     * @param id identificador de la categoría
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría de forma permanente.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categoryServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




