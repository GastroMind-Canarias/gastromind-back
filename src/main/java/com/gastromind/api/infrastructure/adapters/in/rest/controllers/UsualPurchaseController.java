package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.UsualPurchaseServiceImpl;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UsualPurchaseRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usual-purchase")
@Tag(name = "Producto más comprado", description = "Gestión de los registros de producto más comprado de un usuario.")
public class UsualPurchaseController {

    @Autowired
    private UsualPurchaseServiceImpl usualPurchaseServiceImpl;

    @Autowired
    private UsualPurchaseRestMapper usualPurchaseMapper;

    @Operation(summary = "Obtener todos los registros de producto más comprado", description = "Devuelve una lista completa de todos los registros de producto más comprado.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<UsualPurchaseResponse>> getAll() {
        List<UsualPurchase> purchases = usualPurchaseServiceImpl.findAll();
        return ResponseEntity.ok(usualPurchaseMapper.toResponseList(purchases));
    }

    @Operation(summary = "Buscar producto más comprado por ID", description = "Devuelve un único producto más comprado basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<UsualPurchaseResponse> getById(
            @Parameter(description = "ID del producto más comprado a buscar", example = "1") @PathVariable String id) {
        UsualPurchase purchase = usualPurchaseServiceImpl.findById(id);
        return ResponseEntity.ok(usualPurchaseMapper.toResponse(purchase));
    }

    @Operation(summary = "Crear nuevo producto más comprado", description = "Registra un nuevo producto más comprado en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<UsualPurchaseResponse> create(@Valid @RequestBody UsualPurchaseRequest request) {
        UsualPurchase domain = usualPurchaseMapper.toDomain(request);
        UsualPurchase saved = usualPurchaseServiceImpl.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(usualPurchaseMapper.toResponse(saved));
    }

    @Operation(summary = "Actualizar producto más comprado", description = "Modifica los datos de un producto más comprado existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<UsualPurchaseResponse> update(@PathVariable String id, @Valid @RequestBody UsualPurchaseRequest request) {
        UsualPurchase domain = usualPurchaseMapper.toDomain(request);
        UsualPurchase updated = usualPurchaseServiceImpl.update(id, domain);
        return ResponseEntity.ok(usualPurchaseMapper.toResponse(updated));
    }

    @Operation(summary = "Eliminar producto más comprado", description = "Borra físicamente un producto más comprado de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        usualPurchaseServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}