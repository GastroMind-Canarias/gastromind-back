package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.RecipeServiceImpl;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.RecipeRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
@Tag(name = "Receta", description = "Gestion del catalogo de recetas culinarias.")
/**
 * Controlador REST para gestionar recetas.
 */
public class RecipeController {

    @Autowired
    private RecipeServiceImpl recipeServiceImpl;

    @Autowired
    private RecipeRestMapper recipeMapper;
    /**
     * Lista todas las recetas.
     *
     * @return coleccion de recetas
     */

    @Operation(summary = "Obtener todas las recetas", description = "Devuelve una lista completa de todas las recetas registradas.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecipeResponse>> getAll() {
        List<Recipe> recipes = recipeServiceImpl.findAll();
        return ResponseEntity.ok(recipeMapper.toResponseList(recipes));
    }
    /**
     * Recupera una receta por ID.
     *
     * @param id identificador de la receta
     * @return receta encontrada
     */

    @Operation(summary = "Buscar receta por ID", description = "Devuelve una receta concreta a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeResponse> getById(
            @Parameter(description = "ID de la receta a buscar", example = "1") @PathVariable String id) {
        Recipe recipe = recipeServiceImpl.findById(id);
        return ResponseEntity.ok(recipeMapper.toResponse(recipe));
    }
    /**
     * Crea una receta.
     *
     * @param request datos de alta
     * @return receta creada
     */

    @Operation(summary = "Crear nueva receta", description = "Registra una nueva receta en el sistema.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecipeResponse> create(@Valid @RequestBody RecipeRequest request) {
        Recipe recipeDomain = recipeMapper.toDomain(request);
        Recipe savedRecipe = recipeServiceImpl.create(recipeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeMapper.toResponse(savedRecipe));
    }
    /**
     * Define una receta existente.
     *
     * @param id identificador de la receta
     * @param request datos actualizados
     * @return receta actualizada
     */

    @Operation(summary = "Actualizar receta", description = "Modifica los datos de una receta existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecipeResponse> update(@PathVariable String id, @Valid @RequestBody RecipeRequest request) {
        Recipe recipeDomain = recipeMapper.toDomain(request);
        Recipe updatedRecipe = recipeServiceImpl.update(id, recipeDomain);
        return ResponseEntity.ok(recipeMapper.toResponse(updatedRecipe));
    }
    /**
     * Elimina una receta.
     *
     * @param id identificador de la receta
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar receta", description = "Elimina una receta de forma permanente.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        recipeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




