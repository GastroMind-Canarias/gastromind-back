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
@Tag(name = "Categoria", description = "Gestion del catalogo de categorias para productos y recetas.")
/**
 * Controlador REST para gestionar categorias.
 */
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private CategoryRestMapper categoryMapper;
    /**
     * Lista todas las categorias.
     *
     * @return coleccion de categorias
     */

    @Operation(summary = "Obtener todas las categorias", description = "Devuelve la lista completa de categorias registradas.")
    @GetMapping
    @ApiStandardDoc
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<Category> categories = categoryServiceImpl.findAll();
        return ResponseEntity.ok(categoryMapper.toResponseList(categories));
    }
    /**
     * Recupera una categoria por ID.
     *
     * @param id identificador de la categoria
     * @return categoria encontrada
     */

    @Operation(summary = "Buscar categoria por ID", description = "Devuelve una categoria concreta a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @Parameter(description = "ID de la categoria a buscar", example = "1") @PathVariable String id) {
        Category category = categoryServiceImpl.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }
    /**
     * Crea una categoria.
     *
     * @param categoryRequest datos de alta
     * @return categoria creada
     */

    @Operation(summary = "Crear nueva categoria", description = "Registra una nueva categoria en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category categoryDomain = categoryMapper.toDomain(categoryRequest);
        Category savedCategory = categoryServiceImpl.create(categoryDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(savedCategory));
    }
    /**
     * Define una categoria existente.
     *
     * @param id identificador de la categoria
     * @param categoryRequest datos actualizados
     * @return categoria actualizada
     */

    @Operation(summary = "Actualizar categoria", description = "Modifica los datos de una categoria existente.")
    @PutMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<CategoryResponse> update(@PathVariable String id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category categoryDomain = categoryMapper.toDomain(categoryRequest);
        Category updatedCategory = categoryServiceImpl.update(id, categoryDomain);
        return ResponseEntity.ok(categoryMapper.toResponse(updatedCategory));
    }
    /**
     * Elimina una categoria.
     *
     * @param id identificador de la categoria
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar categoria", description = "Elimina una categoria de forma permanente.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categoryServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




