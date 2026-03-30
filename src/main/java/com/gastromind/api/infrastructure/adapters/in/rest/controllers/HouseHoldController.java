package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.HouseHoldServiceImpl;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.HouseHoldRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.HouseholdApplianceRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserRestMapper;
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
@RequestMapping("/api/v1/households")
@Tag(name = "Unidad Familiar", description = "Gestión de las unidades familiares y la agrupación de usuarios.")
public class HouseHoldController {

    @Autowired
    private HouseHoldServiceImpl holdServiceImpl;

    @Autowired
    private HouseHoldRestMapper houseHoldMapper;

    @Autowired
    private HouseholdApplianceRestMapper applianceRestMapper;

    @Autowired
    private UserRestMapper userRestMapper;

    @Operation(summary = "Obtener todas las unidades familiares", description = "Devuelve una lista completa de todas las unidades familiares registradas.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<HouseHoldResponse>> getAll() {
        List<HouseHold> households = holdServiceImpl.findAll();
        return ResponseEntity.ok(houseHoldMapper.toResponseList(households));
    }

    @Operation(summary = "Buscar unidad familiar por ID", description = "Devuelve una única unidad familiar basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<HouseHoldResponse> getById(
            @Parameter(description = "ID de la unidad familiar a buscar", example = "1") @PathVariable String id) {
        HouseHold houseHold = holdServiceImpl.findById(id);
        return ResponseEntity.ok(houseHoldMapper.toResponse(houseHold));
    }

    @Operation(summary = "Crear nueva unidad familiar", description = "Registra una nueva unidad familiar en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<HouseHoldResponse> create(@Valid @RequestBody HouseHoldRequest request) {
        HouseHold houseHoldDomain = houseHoldMapper.toDomain(request);
        HouseHold savedHouseHold = holdServiceImpl.create(houseHoldDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(houseHoldMapper.toResponse(savedHouseHold));
    }

    @Operation(summary = "Crear nueva unidad familiar con propietario", description = "Registra una nueva unidad familiar y vincula al creador como OWNER.")
    @ApiPostDoc
    @PostMapping("/create-with-owner")
    public ResponseEntity<HouseHoldResponse> createWithOwner(@Valid @RequestBody HouseHoldRequest request,
            @RequestParam String userId) {
        HouseHold houseHoldDomain = houseHoldMapper.toDomain(request);
        HouseHold savedHouseHold = holdServiceImpl.createWithCreator(houseHoldDomain, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(houseHoldMapper.toResponse(savedHouseHold));
    }

    @Operation(summary = "Listar miembros del hogar", description = "Recupera todos los usuarios que pertenecen a una unidad familiar específica.")
    @ApiStandardDoc
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserResponse>> listMembers(@PathVariable String id) {
        List<User> members = holdServiceImpl.listMembers(id);
        return ResponseEntity.ok(userRestMapper.toResponseList(members));
    }

    @Operation(summary = "Añadir miembro al hogar", description = "Víncula un usuario a una unidad familiar específica.")
    @ApiPostDoc
    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<UserResponse> addMember(@PathVariable String id, @PathVariable String userId) {
        User savedUser = holdServiceImpl.addMember(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRestMapper.toResponse(savedUser));
    }

    @Operation(summary = "Añadir dispositivo al hogar", description = "Añade un electrodoméstico a una unidad familiar específica.")
    @ApiPostDoc
    @PostMapping("/{id}/appliances")
    public ResponseEntity<ApplianceResponse> addAppliance(@PathVariable String id, @RequestParam Appliance appliance) {
        HouseholdAppliance savedAppliance = holdServiceImpl.addAppliance(id, appliance);
        return ResponseEntity.status(HttpStatus.CREATED).body(applianceRestMapper.toResponse(savedAppliance));
    }

    @Operation(summary = "Listar dispositivos del hogar", description = "Recupera todos los electrodomésticos de una unidad familiar específica.")
    @ApiStandardDoc
    @GetMapping("/{id}/appliances")
    public ResponseEntity<List<ApplianceResponse>> listAppliances(@PathVariable String id) {
        List<HouseholdAppliance> appliances = holdServiceImpl.listAppliances(id);
        return ResponseEntity.ok(applianceRestMapper.toResponseList(appliances));
    }

    @Operation(summary = "Eliminar dispositivo del hogar", description = "Elimina un electrodoméstico de la unidad familiar.")
    @ApiStandardDoc
    @DeleteMapping("/appliances/{applianceId}")
    public ResponseEntity<Void> removeAppliance(@PathVariable String applianceId) {
        holdServiceImpl.removeAppliance(applianceId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Generar token de invitación", description = "Genera un token de invitación vinculado a la unidad familiar.")
    @ApiPostDoc
    @PostMapping("/{id}/invite")
    public ResponseEntity<String> inviteMember(@PathVariable String id) {
        String token = holdServiceImpl.generateInviteToken(id);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Eliminar miembro del hogar", description = "Permite al propietario eliminar a otro usuario del hogar.")
    @ApiStandardDoc
    @DeleteMapping("/{id}/members/{memberUserId}")
    public ResponseEntity<Void> removeMember(@PathVariable String id, @PathVariable String memberUserId,
            @RequestParam String ownerId) {
        holdServiceImpl.removeMember(ownerId, id, memberUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar unidad familiar", description = "Modifica los datos de una unidad familiar existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<HouseHoldResponse> update(@PathVariable String id,
            @Valid @RequestBody HouseHoldRequest request) {
        HouseHold houseHoldDomain = houseHoldMapper.toDomain(request);
        HouseHold updatedHouseHold = holdServiceImpl.update(id, houseHoldDomain);
        return ResponseEntity.ok(houseHoldMapper.toResponse(updatedHouseHold));
    }

    @Operation(summary = "Eliminar unidad familiar", description = "Borra físicamente una unidad familiar de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        holdServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}