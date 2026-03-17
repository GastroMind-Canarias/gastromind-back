package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.FridgeServiceImpl;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.FridgeRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/fridges")
@Tag(name = "Nevera", description = "Gestión del inventario de la nevera y productos almacenados.")
public class FridgeController {

    @Autowired
    private FridgeServiceImpl fridgeServiceImpl;

    @Autowired
    private FridgeRestMapper fridgeRestMapper;

    @Operation(summary = "Obtener todas las neveras", description = "Devuelve una lista completa de todas las neveras registradas.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<FridgeResponse>> getAll() {
        List<Fridge> fridges = fridgeServiceImpl.findAll();
        return ResponseEntity.ok(fridgeRestMapper.toResponseList(fridges));
    }

    @Operation(summary = "Buscar nevera por ID", description = "Devuelve una única nevera basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<FridgeResponse> getById(
            @Parameter(description = "ID de la nevera a buscar", example = "1") @PathVariable String id) {
        Fridge fridge = fridgeServiceImpl.findById(id);
        return ResponseEntity.ok(fridgeRestMapper.toResponse(fridge));
    }

    @Operation(summary = "Crear nueva nevera", description = "Registra una nueva nevera en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<FridgeResponse> create(@Valid @RequestBody FridgeRequest request) {
        Fridge fridgeDomain = fridgeRestMapper.toDomain(request);
        Fridge savedFridge = fridgeServiceImpl.create(fridgeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(fridgeRestMapper.toResponse(savedFridge));
    }

    @Operation(summary = "Actualizar nevera", description = "Modifica los datos de una nevera existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<FridgeResponse> update(@PathVariable String id, @Valid @RequestBody FridgeRequest request) {
        Fridge fridgeDomain = fridgeRestMapper.toDomain(request);
        Fridge updatedFridge = fridgeServiceImpl.update(id, fridgeDomain);
        return ResponseEntity.ok(fridgeRestMapper.toResponse(updatedFridge));
    }

    @Operation(summary = "Eliminar nevera", description = "Borra físicamente una nevera de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        fridgeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}