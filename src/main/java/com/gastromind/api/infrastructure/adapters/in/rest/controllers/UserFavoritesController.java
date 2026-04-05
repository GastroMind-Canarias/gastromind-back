package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.SaveSuggestedRecipeAsFavoriteUseCase;
import com.gastromind.api.application.services.UserFavoritesServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserFavoritesRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-favorites")
@Tag(name = "Receta favorita", description = "Gestión del catálogo de recetas favoritas para usuarios.")
public class UserFavoritesController {

    @Autowired
    private UserFavoritesServiceImpl userFavoritesServiceImpl;

    @Autowired
    private UserFavoritesRestMapper favoritesMapper;

    @Autowired
    private SaveSuggestedRecipeAsFavoriteUseCase saveSuggestedRecipeAsFavoriteUseCase;

    @Autowired
    private UserServiceImpl userServiceImpl;

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        if (authentication == null) {
            throw new ForbiddenException("Usuario no autenticado");
        }
        return userServiceImpl.findByUsername(authentication.getName());
    }

    @Operation(summary = "Guardar sugerencia de IA como favorita", description = """
            Persiste la receta generada por IA (clave temporal en Redis) en el catálogo y la asocia al usuario actual.
            La sugerencia debe seguir vigente (TTL ~10 días) y pertenecer al mismo usuario/hogar.""")
    @ApiPostDoc
    @PostMapping("/me/from-suggestion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserFavoritesResponse> saveFromSuggestion(
            Authentication authentication,
            @RequestParam String suggestionId) {
        User user = getCurrentUser(authentication);
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }
        String householdId = user.getHouseHold_id().getId();
        UserFavorites saved = saveSuggestedRecipeAsFavoriteUseCase.execute(
                suggestionId, householdId, user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }

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
            @Parameter(description = "ID de la receta favorita a buscar", example = "1") @PathVariable String id) {
        UserFavorites favorite = userFavoritesServiceImpl.findById(id);
        return ResponseEntity.ok(favoritesMapper.toResponse(favorite));
    }

    @Operation(summary = "Crear nueva receta favorita", description = "Registra una nueva receta favorita en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<UserFavoritesResponse> create(@Valid @RequestBody UserFavoritesRequest request) {
        UserFavorites domain = favoritesMapper.toDomain(request);
        UserFavorites saved = userFavoritesServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }

    @Operation(summary = "Actualizar receta favorita", description = "Modifica los datos de una receta favorita existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<UserFavoritesResponse> update(@PathVariable String id, @Valid @RequestBody UserFavoritesRequest request) {
        UserFavorites domain = favoritesMapper.toDomain(request);
        UserFavorites updated = userFavoritesServiceImpl.update(id, domain);
        return ResponseEntity.ok(favoritesMapper.toResponse(updated));
    }

    @Operation(summary = "Eliminar receta favorita", description = "Borra físicamente una receta favorita de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userFavoritesServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}