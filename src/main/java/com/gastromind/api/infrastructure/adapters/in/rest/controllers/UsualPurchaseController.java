package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UsualPurchaseServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UsualPurchaseRestMapper;
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
@RequestMapping("/api/v1/usual-purchases")
@Tag(name = "Producto más comprado", description = "Gestión de los registros de producto más comprado de un usuario.")
public class UsualPurchaseController {

    @Autowired
    private UsualPurchaseServiceImpl usualPurchaseServiceImpl;

    @Autowired
    private UsualPurchaseRestMapper usualPurchaseMapper;

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

    @Operation(summary = "Obtener todos los registros (solo ADMIN)", description = "Lista completa de compras habituales.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsualPurchaseResponse>> getAll() {
        List<UsualPurchase> purchases = usualPurchaseServiceImpl.findAll();
        return ResponseEntity.ok(usualPurchaseMapper.toResponseList(purchases));
    }

    @Operation(summary = "Listar mis compras habituales", description = "Registros del usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<UsualPurchaseResponse>> listMine(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(usualPurchaseMapper.toResponseList(
                usualPurchaseServiceImpl.findAllByUserId(user.getId())));
    }

    @Operation(summary = "Obtener uno de mis registros por ID", description = "Solo si pertenece al usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UsualPurchaseResponse> getMineById(Authentication authentication, @PathVariable String id) {
        User user = getCurrentUser(authentication);
        UsualPurchase purchase = usualPurchaseServiceImpl.findByIdForUser(id, user.getId());
        return ResponseEntity.ok(usualPurchaseMapper.toResponse(purchase));
    }

    @Operation(summary = "Crear mi compra habitual", description = "Asocia el registro al usuario autenticado.")
    @ApiPostDoc
    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UsualPurchaseResponse> createMine(
            Authentication authentication,
            @Valid @RequestBody UsualPurchaseMeRequest request) {
        User user = getCurrentUser(authentication);
        UsualPurchase domain = usualPurchaseMapper.toDomainForMe(request, user.getId());
        UsualPurchase saved = usualPurchaseServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(usualPurchaseMapper.toResponse(saved));
    }

    @Operation(summary = "Buscar por ID (solo ADMIN)", description = "Detalle de un registro por identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsualPurchaseResponse> getById(
            @Parameter(description = "ID del registro", example = "1") @PathVariable String id) {
        UsualPurchase purchase = usualPurchaseServiceImpl.findById(id);
        return ResponseEntity.ok(usualPurchaseMapper.toResponse(purchase));
    }

    @Operation(summary = "Crear registro (solo ADMIN)", description = "Incluye user_id en el cuerpo.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsualPurchaseResponse> create(@Valid @RequestBody UsualPurchaseRequest request) {
        UsualPurchase domain = usualPurchaseMapper.toDomain(request);
        UsualPurchase saved = usualPurchaseServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(usualPurchaseMapper.toResponse(saved));
    }

    @Operation(summary = "Actualizar registro (solo ADMIN)", description = "Modifica un registro existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsualPurchaseResponse> update(@PathVariable String id, @Valid @RequestBody UsualPurchaseRequest request) {
        UsualPurchase domain = usualPurchaseMapper.toDomain(request);
        UsualPurchase updated = usualPurchaseServiceImpl.update(id, domain);
        return ResponseEntity.ok(usualPurchaseMapper.toResponse(updated));
    }

    @Operation(summary = "Actualizar uno de mis registros", description = "Solo si pertenece al usuario autenticado.")
    @ApiStandardDoc
    @PutMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<UsualPurchaseResponse> updateMine(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UsualPurchaseMeRequest request) {
        User user = getCurrentUser(authentication);
        UsualPurchase domain = usualPurchaseMapper.toDomainForMe(request, user.getId());
        UsualPurchase updated = usualPurchaseServiceImpl.updateForUser(id, domain, user.getId());
        return ResponseEntity.ok(usualPurchaseMapper.toResponse(updated));
    }

    @Operation(summary = "Eliminar registro (solo ADMIN)", description = "Borra un registro de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        usualPurchaseServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar uno de mis registros", description = "Solo si pertenece al usuario autenticado.")
    @ApiStandardDoc
    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<Void> deleteMine(Authentication authentication, @PathVariable String id) {
        User user = getCurrentUser(authentication);
        usualPurchaseServiceImpl.deleteForUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
