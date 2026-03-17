package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.AllergenRestMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.AllergenServiceImpl;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/allergens")
@Tag(name = "Alérgeno", description = "Gestión del catálogo de alérgenos e intolerancias alimentarias.")
public class AllergenController {

    @Autowired
    private AllergenServiceImpl allergenServiceImpl;

    @Autowired
    private AllergenRestMapper allergenMapper;

    @Operation(summary = "Obtener todos los alérgenos", description = "Devuelve una lista completa de todos los alérgenos registrados en el sistema.")
    @GetMapping
    @ApiStandardDoc
    public ResponseEntity<List<AllergenResponse>> getAll() {
        List<Allergen> allergens = allergenServiceImpl.findAll();
        return ResponseEntity.ok(allergenMapper.toResponseList(allergens));
    }

    @Operation(summary = "Buscar alérgeno por ID", description = "Devuelve un único alérgeno basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<AllergenResponse> getById(
            @Parameter(description = "ID del alérgeno a buscar", example = "1") @PathVariable String id) {
        Allergen allergen = allergenServiceImpl.findById(id);
        return ResponseEntity.ok(allergenMapper.toResponse(allergen));
    }

    @Operation(summary = "Crear nuevo alérgeno", description = "Registra un nuevo alérgeno en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<AllergenResponse> create(@Valid @RequestBody AllergenRequest request) {
        Allergen allergenDomain = allergenMapper.toDomain(request);
        Allergen savedAllergen = allergenServiceImpl.create(allergenDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(allergenMapper.toResponse(savedAllergen));
    }

    @Operation(summary = "Actualizar alérgeno", description = "Modifica los datos de un alérgeno existente.")
    @PutMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<AllergenResponse> update(@PathVariable String id, @Valid @RequestBody AllergenRequest request) {
        Allergen allergenDomain = allergenMapper.toDomain(request);
        Allergen updatedAllergen = allergenServiceImpl.update(id, allergenDomain);
        return ResponseEntity.ok(allergenMapper.toResponse(updatedAllergen));
    }

    @Operation(summary = "Eliminar alérgeno", description = "Borra físicamente un alérgeno de la base de datos.")
    @DeleteMapping("/{id}")
    @ApiStandardDoc
    public ResponseEntity<Void> delete(@PathVariable String id) {
        allergenServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}