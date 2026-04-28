package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.StoreServiceImpl;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.StoreRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Tienda", description = "Gestión del catálogo de tiendas y establecimientos.")
/**
 * Controlador REST para gestionar tiendas.
 */
public class StoreController {

    @Autowired
    private StoreServiceImpl storeServiceImpl;

    @Autowired
    private StoreRestMapper storeMapper;
    /**
     * Lista todas las tiendas.
     *
     * @return colección de tiendas
     */

    @Operation(summary = "Obtener todas las tiendas", description = "Devuelve una lista completa de todas las tiendas registradas.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StoreResponse>> getAll() {
        List<Store> stores = storeServiceImpl.findAll();
        return ResponseEntity.ok(storeMapper.toResponseList(stores));
    }
    /**
     * Recupera una tienda por ID.
     *
     * @param id identificador de la tienda
     * @return tienda encontrada
     */

    @Operation(summary = "Buscar tienda por ID", description = "Devuelve una tienda concreta a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StoreResponse> getById(
            @Parameter(description = "ID de la tienda a buscar", example = "1")
            @PathVariable String id) {
        Store store = storeServiceImpl.findById(id);
        return ResponseEntity.ok(storeMapper.toResponse(store));
    }
    /**
     * Crea una tienda.
     *
     * @param request datos de alta
     * @return tienda creada
     */

    @Operation(summary = "Crear nueva tienda", description = "Registra una nueva tienda en el sistema.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody StoreRequest request) {
        Store storeDomain = storeMapper.toDomain(request);
        Store savedStore = storeServiceImpl.create(storeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(storeMapper.toResponse(savedStore));
    }
    /**
     * Define una tienda existente.
     *
     * @param id identificador de la tienda
     * @param request datos actualizados
     * @return tienda actualizada
     */

    @Operation(summary = "Actualizar tienda", description = "Modifica los datos de una tienda existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreResponse> update(@PathVariable String id, @Valid @RequestBody StoreRequest request) {
        Store storeDomain = storeMapper.toDomain(request);
        Store updatedStore = storeServiceImpl.update(id, storeDomain);
        return ResponseEntity.ok(storeMapper.toResponse(updatedStore));
    }
    /**
     * Elimina una tienda.
     *
     * @param id identificador de la tienda
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar tienda", description = "Elimina una tienda de forma permanente.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        storeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




