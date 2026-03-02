package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.AllergenRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuario", description = "Gestión de los perfiles de usuario en el sistema.")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRestMapper userMapper;

    @Autowired
    private AllergenRestMapper allergenMapper;

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista completa de todos los usuarios registrados.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<User> users = userServiceImpl.findAll();
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @Operation(summary = "Buscar usuario por ID", description = "Devuelve un único usuario basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(
            @Parameter(description = "ID del usuario a buscar", example = "1") @PathVariable String id) {
        User user = userServiceImpl.findById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User userDomain = userMapper.toDomain(request);
        User savedUser = userServiceImpl.create(userDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(savedUser));
    }

    @Operation(summary = "Actualizar perfil de usuario", description = "Modifica los campos name o email de un usuario existente.")
    @ApiStandardDoc
    @PatchMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        User updatedUser = userServiceImpl.updateProfile(id, name, email);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        User userDomain = userMapper.toDomain(request);
        User updatedUser = userServiceImpl.update(id, userDomain);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Operation(summary = "Eliminar usuario", description = "Borra físicamente un usuario de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────────────────────
    // Use Cases: Gestión de Alérgenos de Usuario
    // ──────────────────────────────────────────────────────────────

    /**
     * RegistrarNuevoAlergenoDeUsuario: añade un alérgeno a user_allergens.
     */
    @Operation(summary = "Registrar alérgeno de usuario", description = "Añade un alérgeno a la lista personal del usuario (tabla user_allergens). Este dato se usará para filtrar recetas generadas por la IA.")
    @ApiPostDoc
    @PostMapping("/{userId}/allergens/{allergenId}")
    public ResponseEntity<Void> addAllergen(
            @Parameter(description = "ID del usuario", example = "usr-123") @PathVariable String userId,
            @Parameter(description = "ID del alérgeno a registrar", example = "alg-456") @PathVariable String allergenId) {
        userServiceImpl.addAllergen(userId, allergenId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * EliminarAlergenoDeUsuario: elimina un alérgeno de user_allergens.
     */
    @Operation(summary = "Eliminar alérgeno de usuario", description = "Elimina un alérgeno de la lista personal del usuario (tabla user_allergens).")
    @ApiStandardDoc
    @DeleteMapping("/{userId}/allergens/{allergenId}")
    public ResponseEntity<Void> removeAllergen(
            @Parameter(description = "ID del usuario", example = "usr-123") @PathVariable String userId,
            @Parameter(description = "ID del alérgeno a eliminar", example = "alg-456") @PathVariable String allergenId) {
        userServiceImpl.removeAllergen(userId, allergenId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ListarAlergenosDeUsuario: lista los alérgenos del usuario.
     */
    @Operation(summary = "Listar alérgenos del usuario", description = "Recupera todos los alérgenos registrados para un usuario específico.")
    @ApiStandardDoc
    @GetMapping("/{userId}/allergens")
    public ResponseEntity<List<AllergenResponse>> listAllergens(
            @Parameter(description = "ID del usuario", example = "usr-123") @PathVariable String userId) {
        List<Allergen> allergens = userServiceImpl.listAllergens(userId);
        return ResponseEntity.ok(allergenMapper.toResponseList(allergens));
    }
}