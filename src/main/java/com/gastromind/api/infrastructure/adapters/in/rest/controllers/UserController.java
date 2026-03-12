package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
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
}