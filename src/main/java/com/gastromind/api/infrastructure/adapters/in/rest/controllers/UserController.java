package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.UpdateMyPreferencesUseCase;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenIdListRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UpdateMyPreferencesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.AllergenRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuario", description = "GestiAAaAaAaaAAaAAasAAn de los perfiles de usuario en el sistema.")
/**
 * Controlador REST para operaciones de user.
 */
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRestMapper userMapper;

    @Autowired
    private AllergenRestMapper allergenRestMapper;

    @Autowired
    private UpdateMyPreferencesUseCase updateMyPreferencesUseCase;
    /**
     * Devuelve user por my profile.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Obtener mi perfil", description = "Devuelve la informaciAAaAaAaaAAaAAasAAn del usuario autenticado basAAaAaAaaAAaAAasAAndose en el token JWT.")
    @ApiStandardDoc
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        User user = userServiceImpl.findByUsername(username);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
    /**
     * Devuelve user por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Buscar usuario por ID (Solo ADMIN)", description = "Permite a un administrador consultar cualquier perfil por su ID.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getById(@Parameter(description = "ID del usuario a buscar") @PathVariable String id) {
        User user = userServiceImpl.findById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
    /**
     * Realiza update my profile.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Actualizar mi perfil", description = "Permite al usuario modificar su nombre, email y alAAaAaAaaAAaAAasAArgenos.")
    @ApiStandardDoc
    @PatchMapping("/me/profile")
    public ResponseEntity<UserResponse> updateMyProfile(Authentication authentication, @Valid @RequestBody UserRequest request) {
        User existingUser = userServiceImpl.findByUsername(authentication.getName());
        User userChanges = userMapper.toDomain(request);
        User updatedUser = userServiceImpl.updateProfile(existingUser.getId(), userChanges);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }
    /**
     * Realiza update my preferences.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Editar mis preferencias", description = "Reemplaza en una sola operaciAAaAaAaaAAaAAasAAn los alAAaAaAaaAAaAAasAArgenos del usuario y utensilios del hogar.")
    @ApiStandardDoc
    @PatchMapping("/me/preferences")
    public ResponseEntity<UserResponse> updateMyPreferences(
            Authentication authentication,
            @Valid @RequestBody UpdateMyPreferencesRequest request) {
        User updatedUser = updateMyPreferencesUseCase.execute(authentication.getName(), request.allergenIds(), request.appliances());
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }
    /**
     * Realiza list my allergens.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar mis alAAaAaAaaAAaAAasAArgenos")
    @ApiStandardDoc
    @GetMapping("/me/allergens")
    public ResponseEntity<List<AllergenResponse>> listMyAllergens(Authentication authentication) {
        User user = userServiceImpl.findByUsername(authentication.getName());
        List<Allergen> list = userServiceImpl.listAllergens(user.getId());
        return ResponseEntity.ok(allergenRestMapper.toResponseList(list));
    }
    /**
     * Realiza add my allergen.
     * @param authentication usuario autenticado.
     * @param allergenId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "AAAaAaAaaAAaAAasAAadir un alAAaAaAaaAAaAAasAArgeno a mi perfil")
    @ApiPostDoc
    @PostMapping("/me/allergens")
    public ResponseEntity<Void> addMyAllergen(
            Authentication authentication,
            @RequestParam @NotBlank String allergenId) {
        User user = userServiceImpl.findByUsername(authentication.getName());
        userServiceImpl.addAllergen(user.getId(), allergenId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * Realiza add my allergens batch.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "AAAaAaAaaAAaAAasAAadir varios alAAaAaAaaAAaAAasAArgenos a mi perfil")
    @ApiPostDoc
    @PostMapping("/me/allergens/batch")
    public ResponseEntity<Void> addMyAllergensBatch(
            Authentication authentication,
            @Valid @RequestBody AllergenIdListRequest request) {
        User user = userServiceImpl.findByUsername(authentication.getName());
        userServiceImpl.addAllergensBulk(user.getId(), request.allergenIds());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * Realiza replace my allergens.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Sustituir todos mis alAAaAaAaaAAaAAasAArgenos por el listado indicado")
    @ApiStandardDoc
    @PutMapping("/me/allergens")
    public ResponseEntity<Void> replaceMyAllergens(
            Authentication authentication,
            @Valid @RequestBody AllergenIdListRequest request) {
        User user = userServiceImpl.findByUsername(authentication.getName());
        userServiceImpl.replaceAllergens(user.getId(), request.allergenIds());
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza remove my allergen.
     * @param authentication usuario autenticado.
     * @param allergenId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Quitar un alAAaAaAaaAAaAAasAArgeno de mi perfil")
    @ApiStandardDoc
    @DeleteMapping("/me/allergens/{allergenId}")
    public ResponseEntity<Void> removeMyAllergen(
            Authentication authentication,
            @PathVariable String allergenId) {
        User user = userServiceImpl.findByUsername(authentication.getName());
        userServiceImpl.removeAllergen(user.getId(), allergenId);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza remove my allergens batch.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Quitar varios alAAaAaAaaAAaAAasAArgenos de mi perfil")
    @ApiStandardDoc
    @DeleteMapping("/me/allergens/batch")
    public ResponseEntity<Void> removeMyAllergensBatch(
            Authentication authentication,
            @Valid @RequestBody AllergenIdListRequest request) {
        User user = userServiceImpl.findByUsername(authentication.getName());
        userServiceImpl.removeAllergensBulk(user.getId(), request.allergenIds());
        return ResponseEntity.noContent().build();
    }
    /**
     * Lista todos los user.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Obtener todos los usuarios (Solo ADMIN)")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        List<User> users = userServiceImpl.findAll();
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }
    /**
     * Elimina un user.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar usuario (Solo ADMIN)")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza change user role.
     * @param id el identificador del recurso
     * @param newRole valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Cambiar rol de usuario (Solo ADMIN)")
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable String id,
            @RequestParam Role newRole) {

        User user = userServiceImpl.updateUserRole(id, newRole);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}




