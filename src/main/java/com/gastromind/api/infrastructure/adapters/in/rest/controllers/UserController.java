package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import com.gastromind.api.domain.models.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "Obtener mi perfil", description = "Devuelve la información del usuario autenticado basándose en el token JWT.")
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

    @Operation(summary = "Buscar usuario por ID (Solo ADMIN)", description = "Permite a un administrador consultar cualquier perfil por su ID.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getById(@Parameter(description = "ID del usuario a buscar") @PathVariable String id) {
        User user = userServiceImpl.findById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Operation(summary = "Actualizar mi perfil", description = "Permite al usuario modificar su nombre, email y alérgenos.")
    @ApiStandardDoc
    @PatchMapping("/me/profile")
    public ResponseEntity<UserResponse> updateMyProfile(Authentication authentication, @Valid @RequestBody UserRequest request) {
        String authName = authentication.getName();
        System.out.println("DEBUG: Buscando por username del token -> " + authName);

        User existingUser = userServiceImpl.findByUsername(authName);
        System.out.println("DEBUG: Usuario encontrado: " + existingUser.getName());
        System.out.println("DEBUG: ID del usuario recuperado -> " + existingUser.getId()); // <-- SI ESTO ES NULL, AQUÍ ESTÁ EL ERROR

        User userChanges = userMapper.toDomain(request);
        User updatedUser = userServiceImpl.updateProfile(existingUser.getId(), userChanges);

        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Operation(summary = "Obtener todos los usuarios (Solo ADMIN)")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        List<User> users = userServiceImpl.findAll();
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @Operation(summary = "Eliminar usuario (Solo ADMIN)")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

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