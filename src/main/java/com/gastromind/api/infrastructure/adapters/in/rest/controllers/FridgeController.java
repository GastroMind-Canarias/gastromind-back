package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.FridgeServiceImpl;
import com.gastromind.api.application.usecases.CreateMyFridgeUseCase;
import com.gastromind.api.application.usecases.DeleteMyFridgeUseCase;
import com.gastromind.api.application.usecases.GetMyFridgeUseCase;
import com.gastromind.api.application.usecases.UpdateMyFridgeUseCase;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.FridgeRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fridges")
@Tag(name = "Nevera", description = "Gestión del inventario de la nevera y productos almacenados.")
public class FridgeController {

    @Autowired
    private FridgeServiceImpl fridgeServiceImpl;

    @Autowired
    private FridgeRestMapper fridgeRestMapper;
    @Autowired
    private GetMyFridgeUseCase getMyFridgeUseCase;
    @Autowired
    private CreateMyFridgeUseCase createMyFridgeUseCase;
    @Autowired
    private UpdateMyFridgeUseCase updateMyFridgeUseCase;
    @Autowired
    private DeleteMyFridgeUseCase deleteMyFridgeUseCase;

    @Operation(summary = "Obtener todas las neveras", description = "Devuelve una lista completa de todas las neveras registradas.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeResponse>> getAll() {
        List<Fridge> fridges = fridgeServiceImpl.findAll();
        return ResponseEntity.ok(fridgeRestMapper.toResponseList(fridges));
    }

    @Operation(summary = "Buscar nevera por ID", description = "Devuelve una única nevera basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeResponse> getById(
            @Parameter(description = "ID de la nevera a buscar", example = "1") @PathVariable String id) {
        Fridge fridge = fridgeServiceImpl.findById(id);
        return ResponseEntity.ok(fridgeRestMapper.toResponse(fridge));
    }

    @Operation(summary = "Obtener mi nevera", description = "Devuelve la nevera asociada al hogar del usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<FridgeResponse> getMyFridge(Authentication authentication) {
        Fridge fridge = getMyFridgeUseCase.execute(authentication.getName());
        return ResponseEntity.ok(fridgeRestMapper.toResponse(fridge));
    }

    @Operation(summary = "Crear nueva nevera", description = "Registra una nueva nevera en el sistema.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeResponse> create(@Valid @RequestBody FridgeRequest request) {
        Fridge fridgeDomain = fridgeRestMapper.toDomain(request);
        Fridge savedFridge = fridgeServiceImpl.create(fridgeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(fridgeRestMapper.toResponse(savedFridge));
    }

    @Operation(summary = "Crear mi nevera", description = "Crea la nevera del hogar asociado al usuario OWNER autenticado.")
    @ApiPostDoc
    @PostMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<FridgeResponse> createMyFridge(Authentication authentication, @Valid @RequestBody FridgeRequest request) {
        Fridge fridgeDomain = fridgeRestMapper.toDomain(request);
        Fridge savedFridge = createMyFridgeUseCase.execute(authentication.getName(), fridgeDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(fridgeRestMapper.toResponse(savedFridge));
    }

    @Operation(summary = "Actualizar nevera", description = "Modifica los datos de una nevera existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeResponse> update(@PathVariable String id, @Valid @RequestBody FridgeRequest request) {
        Fridge fridgeDomain = fridgeRestMapper.toDomain(request);
        Fridge updatedFridge = fridgeServiceImpl.update(id, fridgeDomain);
        return ResponseEntity.ok(fridgeRestMapper.toResponse(updatedFridge));
    }

    @Operation(summary = "Actualizar mi nevera", description = "Actualiza la nevera del hogar asociado al usuario OWNER autenticado.")
    @ApiStandardDoc
    @PutMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<FridgeResponse> updateMyFridge(Authentication authentication, @Valid @RequestBody FridgeRequest request) {
        Fridge fridgeDomain = fridgeRestMapper.toDomain(request);
        Fridge updatedFridge = updateMyFridgeUseCase.execute(authentication.getName(), fridgeDomain);
        return ResponseEntity.ok(fridgeRestMapper.toResponse(updatedFridge));
    }

    @Operation(summary = "Eliminar nevera", description = "Borra físicamente una nevera de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        fridgeServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar mi nevera", description = "Elimina la nevera del hogar asociado al usuario OWNER autenticado.")
    @ApiStandardDoc
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteMyFridge(Authentication authentication) {
        deleteMyFridgeUseCase.execute(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}