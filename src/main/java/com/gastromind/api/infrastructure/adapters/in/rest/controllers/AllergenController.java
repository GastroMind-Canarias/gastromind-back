package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.AllergenServiceImpl;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.AllergenRestMapper;
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
@RequestMapping("/api/v1/allergens")
@Tag(name = "Alergeno", description = "Gestion del catalogo de alergenos e intolerancias alimentarias.")
/**
 * Controlador REST para consultar y mantener el catalogo de alergenos.
 */
public class AllergenController {

    @Autowired
    private AllergenServiceImpl allergenServiceImpl;

    @Autowired
    private AllergenRestMapper allergenMapper;
    /**
     * Lista todos los alergenos registrados.
     *
     * @return coleccion de alergenos
     */

    @Operation(summary = "Obtener todos los alergenos", description = "Devuelve la lista completa de alergenos registrados en el sistema.")
    @GetMapping
    @ApiStandardDoc
    public ResponseEntity<List<AllergenResponse>> getAll() {
        List<Allergen> allergens = allergenServiceImpl.findAll();
        return ResponseEntity.ok(allergenMapper.toResponseList(allergens));
    }
    /**
     * Recupera un alergeno por su identificador.
     *
     * @param id identificador del alergeno
     * @return alergeno encontrado
     */

    @Operation(summary = "Buscar alergeno por ID", description = "Devuelve un alergeno concreto a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<AllergenResponse> getById(
            @Parameter(description = "ID del alergeno a buscar", example = "1") @PathVariable String id) {
        Allergen allergen = allergenServiceImpl.findById(id);
        return ResponseEntity.ok(allergenMapper.toResponse(allergen));
    }
    /**
     * Registra un nuevo alergeno.
     *
     * @param request datos de alta del alergeno
     * @return alergeno creado
     */

    @Operation(summary = "Crear nuevo alergeno", description = "Registra un nuevo alergeno en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<AllergenResponse> create(@Valid @RequestBody AllergenRequest request) {
        Allergen allergenDomain = allergenMapper.toDomain(request);
        Allergen savedAllergen = allergenServiceImpl.create(allergenDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(allergenMapper.toResponse(savedAllergen));
    }
    /**
     * Define un alergeno existente.
     *
     * @param id identificador del alergeno
     * @param request datos actualizados
     * @return alergeno actualizado
     */

    @Operation(summary = "Actualizar alergeno", description = "Modifica los datos de un alergeno existente.")
    @PutMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<AllergenResponse> update(@PathVariable String id, @Valid @RequestBody AllergenRequest request) {
        Allergen allergenDomain = allergenMapper.toDomain(request);
        Allergen updatedAllergen = allergenServiceImpl.update(id, allergenDomain);
        return ResponseEntity.ok(allergenMapper.toResponse(updatedAllergen));
    }
    /**
     * Elimina un alergeno.
     *
     * @param id identificador del alergeno
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar alergeno", description = "Elimina un alergeno de forma permanente.")
    @DeleteMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<Void> delete(@PathVariable String id) {
        allergenServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




