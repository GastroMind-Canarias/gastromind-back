package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.CategoryServiceImpl;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.CategoryRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categoría", description = "Gestión del catálogo de categorías para la clasificación de productos y recetas.")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private CategoryRestMapper categoryMapper;

    @Operation(summary = "Obtener todas las categorías", description = "Devuelve una lista completa de todas las categorías registradas.")
    @GetMapping
    @ApiStandardDoc
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<Category> categories = categoryServiceImpl.findAll();
        return ResponseEntity.ok(categoryMapper.toResponseList(categories));
    }

    @Operation(summary = "Buscar categoría por ID", description = "Devuelve una única categoría basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @Parameter(description = "ID de la categoría a buscar", example = "1") @PathVariable String id) {
        Category category = categoryServiceImpl.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category categoryDomain = categoryMapper.toDomain(categoryRequest);
        Category savedCategory = categoryServiceImpl.create(categoryDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(savedCategory));
    }

    @Operation(summary = "Actualizar categoría", description = "Modifica los datos de una categoría existente.")
    @PutMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<CategoryResponse> update(@PathVariable String id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category categoryDomain = categoryMapper.toDomain(categoryRequest);
        Category updatedCategory = categoryServiceImpl.update(id, categoryDomain);
        return ResponseEntity.ok(categoryMapper.toResponse(updatedCategory));
    }

    @Operation(summary = "Eliminar categoría", description = "Borra físicamente una categoría de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categoryServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}