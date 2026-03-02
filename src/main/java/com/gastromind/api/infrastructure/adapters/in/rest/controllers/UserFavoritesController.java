package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserFavoritesServiceImpl;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.RecipeRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserFavoritesRestMapper;
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
@RequestMapping("/api/v1/user-favorites")
@Tag(name = "Recetas Favoritas", description = "Gestión de recetas favoritas por usuario (GuardarRecetaFavorita, EliminarRecetaFavorita, ListarRecetasFavoritas).")
public class UserFavoritesController {

    @Autowired
    private UserFavoritesServiceImpl userFavoritesServiceImpl;

    @Autowired
    private UserFavoritesRestMapper favoritesMapper;

    @Autowired
    private RecipeRestMapper recipeMapper;

    // ──────────────────────────────────────────────────────────────
    // Use Cases principales
    // ──────────────────────────────────────────────────────────────

    /**
     * GuardarRecetaFavorita: crea un registro en user_favorites.
     */
    @Operation(summary = "Guardar receta favorita", description = "Crea un nuevo registro en user_favorites vinculando user_id y recipe_id. Evita duplicados.")
    @ApiPostDoc
    @PostMapping("/user/{userId}/recipe/{recipeId}")
    public ResponseEntity<UserFavoritesResponse> addFavorite(
            @Parameter(description = "ID del usuario", example = "usr-456-abc") @PathVariable String userId,
            @Parameter(description = "ID de la receta", example = "rec-789-xyz") @PathVariable String recipeId) {
        UserFavorites saved = userFavoritesServiceImpl.addFavorite(userId, recipeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }

    /**
     * EliminarRecetaFavorita: elimina un registro de user_favorites.
     */
    @Operation(summary = "Eliminar receta favorita", description = "Elimina un registro de user_favorites dado el userId y recipeId.")
    @ApiStandardDoc
    @DeleteMapping("/user/{userId}/recipe/{recipeId}")
    public ResponseEntity<Void> removeFavorite(
            @Parameter(description = "ID del usuario", example = "usr-456-abc") @PathVariable String userId,
            @Parameter(description = "ID de la receta", example = "rec-789-xyz") @PathVariable String recipeId) {
        userFavoritesServiceImpl.removeFavorite(userId, recipeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ListarRecetasFavoritas: recupera las recetas favoritas de un usuario.
     */
    @Operation(summary = "Listar recetas favoritas de un usuario", description = "Recupera todas las recetas marcadas como favoritas para un user_id dado.")
    @ApiStandardDoc
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeResponse>> getFavoritesByUser(
            @Parameter(description = "ID del usuario", example = "usr-456-abc") @PathVariable String userId) {
        List<Recipe> recipes = userFavoritesServiceImpl.findFavoritesByUserId(userId);
        return ResponseEntity.ok(recipeMapper.toResponseList(recipes));
    }

    // ──────────────────────────────────────────────────────────────
    // CRUD genérico (mantenido por compatibilidad)
    // ──────────────────────────────────────────────────────────────

    @Operation(summary = "Obtener todas las recetas favoritas", description = "Devuelve una lista completa de todas las recetas favoritas registradas.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<UserFavoritesResponse>> getAll() {
        List<UserFavorites> favorites = userFavoritesServiceImpl.findAll();
        return ResponseEntity.ok(favoritesMapper.toResponseList(favorites));
    }

    @Operation(summary = "Buscar receta favorita por ID", description = "Devuelve una única receta favorita basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<UserFavoritesResponse> getById(
            @Parameter(description = "ID de la receta favorita a buscar", example = "fav-001") @PathVariable String id) {
        UserFavorites favorite = userFavoritesServiceImpl.findById(id);
        return ResponseEntity.ok(favoritesMapper.toResponse(favorite));
    }

    @Operation(summary = "Crear nueva receta favorita (CRUD genérico)", description = "Crea directamente una entrada en user_favorites con IDs.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<UserFavoritesResponse> create(@Valid @RequestBody UserFavoritesRequest request) {
        UserFavorites domain = favoritesMapper.toDomain(request);
        UserFavorites saved = userFavoritesServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }

    @Operation(summary = "Eliminar receta favorita por ID", description = "Borra físicamente una receta favorita de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userFavoritesServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}