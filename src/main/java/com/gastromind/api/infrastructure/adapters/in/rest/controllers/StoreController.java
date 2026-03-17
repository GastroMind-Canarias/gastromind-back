package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.StoreServiceImpl;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.StoreRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Tienda", description = "Gestión del catálogo de tiendas y establecimientos comerciales.")
public class StoreController {

    @Autowired
    private StoreServiceImpl storeServiceImpl;

    @Autowired
    private StoreRestMapper storeMapper;

    @Operation(summary = "Obtener todas las tiendas", description = "Devuelve una lista completa de todas las tiendas registradas.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAll() {
        List<Store> stores = storeServiceImpl.findAll();
        return ResponseEntity.ok(storeMapper.toResponseList(stores));
    }

    @Operation(summary = "Buscar tienda por ID", description = "Devuelve una única tienda basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getById(
            @Parameter(description = "ID de la tienda a buscar", example = "1")
            @PathVariable String id) {
        Store store = storeServiceImpl.findById(id);
        return ResponseEntity.ok(storeMapper.toResponse(store));
    }

    @Operation(summary = "Crear nueva tienda", description = "Registra una nueva tienda en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody StoreRequest request) {
        Store storeDomain = storeMapper.toDomain(request);
        Store savedStore = storeServiceImpl.create(storeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(storeMapper.toResponse(savedStore));
    }

    @Operation(summary = "Actualizar tienda", description = "Modifica los datos de una tienda existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> update(@PathVariable String id, @Valid @RequestBody StoreRequest request) {
        Store storeDomain = storeMapper.toDomain(request);
        Store updatedStore = storeServiceImpl.update(id, storeDomain);
        return ResponseEntity.ok(storeMapper.toResponse(updatedStore));
    }

    @Operation(summary = "Eliminar tienda", description = "Borra físicamente una tienda de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        storeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}