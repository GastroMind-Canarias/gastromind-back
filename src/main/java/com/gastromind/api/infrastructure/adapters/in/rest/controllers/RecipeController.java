package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.RecipeServiceImpl;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.RecipeRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/recipes")
@Tag(name = "Receta", description = "Gestión del catálogo de recetas culinarias y sus pasos de preparación.")
public class RecipeController {

    @Autowired
    private RecipeServiceImpl recipeServiceImpl;

    @Autowired
    private RecipeRestMapper recipeMapper;

    @Operation(summary = "Obtener todas las recetas", description = "Devuelve una lista completa de todas las recetas registradas.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAll() {
        List<Recipe> recipes = recipeServiceImpl.findAll();
        return ResponseEntity.ok(recipeMapper.toResponseList(recipes));
    }

    @Operation(summary = "Buscar receta por ID", description = "Devuelve una única receta basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getById(
            @Parameter(description = "ID de la receta a buscar", example = "1") @PathVariable String id) {
        Recipe recipe = recipeServiceImpl.findById(id);
        return ResponseEntity.ok(recipeMapper.toResponse(recipe));
    }

    @Operation(summary = "Crear nueva receta", description = "Registra una nueva receta en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<RecipeResponse> create(@Valid @RequestBody RecipeRequest request) {
        Recipe recipeDomain = recipeMapper.toDomain(request);
        Recipe savedRecipe = recipeServiceImpl.create(recipeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeMapper.toResponse(savedRecipe));
    }

    @Operation(summary = "Actualizar receta", description = "Modifica los datos de una receta existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> update(@PathVariable String id, @Valid @RequestBody RecipeRequest request) {
        Recipe recipeDomain = recipeMapper.toDomain(request);
        Recipe updatedRecipe = recipeServiceImpl.update(id, recipeDomain);
        return ResponseEntity.ok(recipeMapper.toResponse(updatedRecipe));
    }

    @Operation(summary = "Eliminar receta", description = "Borra físicamente una receta de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        recipeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}