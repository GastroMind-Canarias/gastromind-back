package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserFavoritesServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.SaveSuggestedRecipeAsFavoriteUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesMeRequest;
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
@Tag(name = "Receta favorita", description = "Recetas favoritas por usuario.")
/**
 * Controlador REST para operaciones de user favorites.
 */
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
    /**
     * Lista todos los user favorites.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar todos los favoritos (solo ADMIN)", description = "Lista global de enlaces usuarioAAaAasAAAAAAAAasAAAAasAAAAAAAAaAAAAaAaAreceta.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserFavoritesResponse>> getAll() {
        List<UserFavorites> favorites = userFavoritesServiceImpl.findAll();
        return ResponseEntity.ok(favoritesMapper.toResponseList(favorites));
    }
    /**
     * Realiza list mine.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Mis recetas favoritas", description = "Solo favoritos del usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<UserFavoritesResponse>> listMine(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(favoritesMapper.toResponseList(
                userFavoritesServiceImpl.findAllByUserId(user.getId())));
    }
    /**
     * Devuelve user favorites por mine by id.
     * @param authentication usuario autenticado.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Obtener uno de mis favoritos por ID", description = "Solo si el favorito pertenece al usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UserFavoritesResponse> getMineById(Authentication authentication, @PathVariable String id) {
        User user = getCurrentUser(authentication);
        UserFavorites favorite = userFavoritesServiceImpl.findByIdForUser(id, user.getId());
        return ResponseEntity.ok(favoritesMapper.toResponse(favorite));
    }

    @Operation(summary = "Guardar sugerencia de IA como favorita", description = """
            Persiste la receta generada por IA (clave temporal en Redis) en el catAAaAaAaaAAaAAasAAlogo y la asocia al usuario actual.
            La sugerencia debe seguir vigente (TTL ~10 dAAaAaAaaAAaAAasAAas) y pertenecer al mismo usuario/hogar.""")
    /**
     * Realiza save from suggestion.
     * @param authentication usuario autenticado.
     * @param suggestionId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */
    @ApiPostDoc
    @PostMapping("/me/from-suggestion")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UserFavoritesResponse> saveFromSuggestion(
            Authentication authentication,
            @RequestParam String suggestionId) {
        User user = getCurrentUser(authentication);
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
        }
        String householdId = user.getHouseHold_id().getId();
        UserFavorites saved = saveSuggestedRecipeAsFavoriteUseCase.execute(
                suggestionId, householdId, user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }
    /**
     * Realiza create mine.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "AAAaAaAaaAAaAAasAAadir receta a mis favoritos", description = "Asocia la receta al usuario autenticado.")
    @ApiPostDoc
    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UserFavoritesResponse> createMine(
            Authentication authentication,
            @Valid @RequestBody UserFavoritesMeRequest request) {
        User user = getCurrentUser(authentication);
        UserFavorites domain = favoritesMapper.toDomainForMe(request, user.getId());
        UserFavorites saved = userFavoritesServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }
    /**
     * Devuelve user favorites por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Buscar favorito por ID (solo ADMIN)", description = "Detalle global por identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserFavoritesResponse> getById(
            @Parameter(description = "ID del favorito", example = "1") @PathVariable String id) {
        UserFavorites favorite = userFavoritesServiceImpl.findById(id);
        return ResponseEntity.ok(favoritesMapper.toResponse(favorite));
    }
    /**
     * Registra un nuevo user favorites.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Crear favorito (solo ADMIN)", description = "Incluye user_id y recipe_id en el cuerpo.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserFavoritesResponse> create(@Valid @RequestBody UserFavoritesRequest request) {
        UserFavorites domain = favoritesMapper.toDomain(request);
        UserFavorites saved = userFavoritesServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesMapper.toResponse(saved));
    }
    /**
     * Define un user favorites existente.
     * @param id el identificador del recurso
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Actualizar favorito (solo ADMIN)", description = "Modifica un registro existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserFavoritesResponse> update(@PathVariable String id, @Valid @RequestBody UserFavoritesRequest request) {
        UserFavorites domain = favoritesMapper.toDomain(request);
        UserFavorites updated = userFavoritesServiceImpl.update(id, domain);
        return ResponseEntity.ok(favoritesMapper.toResponse(updated));
    }
    /**
     * Realiza update mine.
     * @param authentication usuario autenticado.
     * @param id el identificador del recurso
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Actualizar uno de mis favoritos", description = "Solo si pertenece al usuario autenticado.")
    @ApiStandardDoc
    @PutMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UserFavoritesResponse> updateMine(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UserFavoritesMeRequest request) {
        User user = getCurrentUser(authentication);
        UserFavorites domain = favoritesMapper.toDomainForMe(request, user.getId());
        UserFavorites updated = userFavoritesServiceImpl.updateForUser(id, domain, user.getId());
        return ResponseEntity.ok(favoritesMapper.toResponse(updated));
    }
    /**
     * Elimina un user favorites.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar favorito (solo ADMIN)", description = "Borra un registro de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userFavoritesServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza delete mine.
     * @param authentication usuario autenticado.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar uno de mis favoritos", description = "Solo si pertenece al usuario autenticado.")
    @ApiStandardDoc
    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<Void> deleteMine(Authentication authentication, @PathVariable String id) {
        User user = getCurrentUser(authentication);
        userFavoritesServiceImpl.deleteForUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}




