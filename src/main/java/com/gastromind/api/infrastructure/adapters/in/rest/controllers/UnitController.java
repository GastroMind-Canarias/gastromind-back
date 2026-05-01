package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UnitServiceImpl;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UnitRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@Tag(name = "Unidad", description = "Gestión del catálogo de unidades de medida.")
/**
 * Controlador REST para gestionar unidades de medida.
 */
public class UnitController {

    @Autowired
    private UnitServiceImpl unitServiceImpl;

    @Autowired
    private UnitRestMapper unitMapper;
    /**
     * Lista todas las unidades.
     *
     * @return colección de unidades
     */

    @Operation(summary = "Obtener todas las unidades", description = "Devuelve una lista completa de todas las unidades de medida registradas.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAll() {
        List<Unit> units = unitServiceImpl.findAll();
        return ResponseEntity.ok(unitMapper.toResponseList(units));
    }
    /**
     * Recupera una unidad por ID.
     *
     * @param id identificador de la unidad
     * @return unidad encontrada
     */

    @Operation(summary = "Buscar unidad por ID", description = "Devuelve una unidad concreta a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getById(
            @Parameter(description = "ID de la unidad a buscar", example = "1")
            @PathVariable String id) {
        Unit unit = unitServiceImpl.findById(id);
        return ResponseEntity.ok(unitMapper.toResponse(unit));
    }
    /**
     * Crea una unidad.
     *
     * @param request datos de alta
     * @return unidad creada
     */

    @Operation(summary = "Crear nueva unidad", description = "Registra una nueva unidad de medida en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<UnitResponse> create(@Valid @RequestBody UnitRequest request) {
        Unit unitDomain = unitMapper.toDomain(request);
        Unit savedUnit = unitServiceImpl.create(unitDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(unitMapper.toResponse(savedUnit));
    }
    /**
     * Define una unidad existente.
     *
     * @param id identificador de la unidad
     * @param request datos actualizados
     * @return unidad actualizada
     */

    @Operation(summary = "Actualizar unidad", description = "Modifica los datos de una unidad de medida existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> update(@PathVariable String id, @Valid @RequestBody UnitRequest request) {
        Unit unitDomain = unitMapper.toDomain(request);
        Unit updatedUnit = unitServiceImpl.update(id, unitDomain);
        return ResponseEntity.ok(unitMapper.toResponse(updatedUnit));
    }
    /**
     * Elimina una unidad.
     *
     * @param id identificador de la unidad
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar unidad", description = "Elimina una unidad de medida de forma permanente.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        unitServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




