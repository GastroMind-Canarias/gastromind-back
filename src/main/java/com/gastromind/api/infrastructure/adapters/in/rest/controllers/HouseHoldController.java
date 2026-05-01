package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.HouseHoldServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceIdListRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceSingleUpdateRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceTypeListRequest;
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
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/households")
@Tag(name = "Unidad Familiar")
/**
 * Controlador REST para operaciones de house hold.
 */
public class HouseHoldController {

    @Autowired
    private HouseHoldServiceImpl holdServiceImpl;
    @Autowired
    private HouseHoldRestMapper houseHoldMapper;
    @Autowired
    private HouseholdApplianceRestMapper applianceRestMapper;
    @Autowired
    private UserRestMapper userRestMapper;
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

    private String getCurrentHouseholdId(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser.getHouseHold_id() == null || currentUser.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
        }
        return currentUser.getHouseHold_id().getId();
    }

    private void requireHouseholdOwner(User user) {
        if (user.getRole() != Role.ROLE_OWNER) {
            throw new ForbiddenException("Solo el OWNER del hogar puede gestionar los electrodomesticos");
        }
    }
    /**
     * Lista todos los house hold.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar todos los hogares (Solo ADMIN)")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HouseHoldResponse>> getAll() {
        return ResponseEntity.ok(houseHoldMapper.toResponseList(holdServiceImpl.findAll()));
    }
    /**
     * Elimina un house hold.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar unidad familiar (Solo Admin)")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        holdServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza add appliance.
     * @param id el identificador del recurso
     * @param appliance valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "AAAaAaAaaAAaAAasAAadir dispositivo (Solo Admin)")
    @ApiPostDoc
    @PostMapping("/{id}/appliances")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplianceResponse> addAppliance(@PathVariable String id, @RequestParam Appliance appliance) {
        HouseholdAppliance saved = holdServiceImpl.addAppliance(id, appliance);
        return ResponseEntity.status(HttpStatus.CREATED).body(applianceRestMapper.toResponse(saved));
    }
    /**
     * Realiza remove member.
     * @param id el identificador del recurso
     * @param memberUserId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Expulsar miembro (Solo Admin)")
    @ApiStandardDoc
    @DeleteMapping("/{id}/members/{memberUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeMember(@PathVariable String id, @PathVariable String memberUserId) {
        holdServiceImpl.removeMember(id, memberUserId);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza invite.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Generar token de invitaciAAaAaAaaAAaAAasAAn (Solo Admin)")
    @ApiStandardDoc
    @PostMapping("/{id}/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> invite(@PathVariable String id) {
        return ResponseEntity.ok(holdServiceImpl.generateInviteToken(id));
    }
    /**
     * Realiza promote to owner.
     * @param id el identificador del recurso
     * @param userId el identificador del usuario
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Ascender a OWNER (Solo Admin)")
    @ApiStandardDoc
    @PatchMapping("/{id}/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> promoteToOwner(@PathVariable String id, @PathVariable String userId) {
        User promoted = holdServiceImpl.promoteToOwner(id, userId);
        return ResponseEntity.ok(userRestMapper.toResponse(promoted));
    }
    /**
     * Devuelve house hold por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Ver detalle del hogar (Solo Admin)")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HouseHoldResponse> getById(@PathVariable String id) {
        HouseHold houseHold = holdServiceImpl.findById(id);
        return ResponseEntity.ok(houseHoldMapper.toResponse(houseHold));
    }
    /**
     * Realiza list members.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar miembros del hogar (Solo Admin)")
    @ApiStandardDoc
    @GetMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> listMembers(@PathVariable String id) {
        List<User> members = holdServiceImpl.listMembers(id);
        return ResponseEntity.ok(userRestMapper.toResponseList(members));
    }
    /**
     * Registra un nuevo house hold.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Crear nuevo hogar")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HouseHoldResponse> create(@Valid @RequestBody HouseHoldRequest request) {
        HouseHold saved = holdServiceImpl.create(houseHoldMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(houseHoldMapper.toResponse(saved));
    }
    /**
     * Realiza leave.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Abandonar el hogar actual")
    @ApiPostDoc
    @PostMapping("/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> leave(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        holdServiceImpl.leaveHousehold(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    /**
     * Devuelve house hold por my household.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Ver detalle de mi hogar")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HouseHoldResponse> getMyHousehold(Authentication authentication) {
        String householdId = getCurrentHouseholdId(authentication);
        HouseHold houseHold = holdServiceImpl.findById(householdId);
        return ResponseEntity.ok(houseHoldMapper.toResponse(houseHold));
    }
    /**
     * Realiza list my members.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar miembros de mi hogar")
    @ApiStandardDoc
    @GetMapping("/me/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> listMyMembers(Authentication authentication) {
        String householdId = getCurrentHouseholdId(authentication);
        List<User> members = holdServiceImpl.listMembers(householdId);
        return ResponseEntity.ok(userRestMapper.toResponseList(members));
    }
    /**
     * Realiza invite my household.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Generar token de invitaciAAaAaAaaAAaAAasAAn de mi hogar (ADMIN u OWNER del hogar)")
    @ApiStandardDoc
    @PostMapping("/me/invite")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<String> inviteMyHousehold(Authentication authentication) {
        String householdId = getCurrentHouseholdId(authentication);
        return ResponseEntity.ok(holdServiceImpl.generateInviteToken(householdId));
    }
    /**
     * Realiza list my appliances.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar electrodomAAaAaAaaAAaAAasAAsticos de mi hogar")
    @ApiStandardDoc
    @GetMapping("/me/appliances")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApplianceResponse>> listMyAppliances(Authentication authentication) {
        String householdId = getCurrentHouseholdId(authentication);
        return ResponseEntity.ok(applianceRestMapper.toResponseList(holdServiceImpl.listAppliances(householdId)));
    }
    /**
     * Realiza add my appliance.
     * @param authentication usuario autenticado.
     * @param appliance valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "AAAaAaAaaAAaAAasAAadir un electrodomAAaAaAaaAAaAAasAAstico a mi hogar (solo OWNER)")
    @ApiPostDoc
    @PostMapping("/me/appliances")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplianceResponse> addMyAppliance(
            Authentication authentication,
            @RequestParam @NotNull Appliance appliance) {
        User user = getCurrentUser(authentication);
        requireHouseholdOwner(user);
        String householdId = getCurrentHouseholdId(authentication);
        HouseholdAppliance saved = holdServiceImpl.addAppliance(householdId, appliance);
        return ResponseEntity.status(HttpStatus.CREATED).body(applianceRestMapper.toResponse(saved));
    }
    /**
     * Realiza add my appliances batch.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "AAAaAaAaaAAaAAasAAadir varios electrodomAAaAaAaaAAaAAasAAsticos a mi hogar (solo OWNER; ignora tipos ya existentes)")
    @ApiPostDoc
    @PostMapping("/me/appliances/batch")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApplianceResponse>> addMyAppliancesBatch(
            Authentication authentication,
            @Valid @RequestBody ApplianceTypeListRequest request) {
        User user = getCurrentUser(authentication);
        requireHouseholdOwner(user);
        String householdId = getCurrentHouseholdId(authentication);
        List<HouseholdAppliance> list = holdServiceImpl.addAppliancesBulk(householdId,
                request.appliances() != null ? request.appliances() : List.of());
        return ResponseEntity.status(HttpStatus.CREATED).body(applianceRestMapper.toResponseList(list));
    }
    /**
     * Realiza update my appliance.
     * @param authentication usuario autenticado.
     * @param applianceRecordId valor a utilizar.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Cambiar el tipo de un electrodomAAaAaAaaAAaAAasAAstico de mi hogar (solo OWNER)")
    @ApiStandardDoc
    @PatchMapping("/me/appliances/{applianceRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplianceResponse> updateMyAppliance(
            Authentication authentication,
            @PathVariable String applianceRecordId,
            @Valid @RequestBody ApplianceSingleUpdateRequest request) {
        User user = getCurrentUser(authentication);
        requireHouseholdOwner(user);
        String householdId = getCurrentHouseholdId(authentication);
        HouseholdAppliance saved = holdServiceImpl.updateAppliance(householdId, applianceRecordId, request.appliance());
        return ResponseEntity.ok(applianceRestMapper.toResponse(saved));
    }
    /**
     * Realiza delete my appliance.
     * @param authentication usuario autenticado.
     * @param applianceRecordId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar un electrodomAAaAaAaaAAaAAasAAstico de mi hogar por id de fila (solo OWNER)")
    @ApiStandardDoc
    @DeleteMapping("/me/appliances/{applianceRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAppliance(
            Authentication authentication,
            @PathVariable String applianceRecordId) {
        User user = getCurrentUser(authentication);
        requireHouseholdOwner(user);
        String householdId = getCurrentHouseholdId(authentication);
        holdServiceImpl.removeApplianceFromHousehold(householdId, applianceRecordId);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza delete my appliances batch.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar varios electrodomAAaAaAaaAAaAAasAAsticos por ids de fila (solo OWNER)")
    @ApiStandardDoc
    @DeleteMapping("/me/appliances/batch")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAppliancesBatch(
            Authentication authentication,
            @Valid @RequestBody ApplianceIdListRequest request) {
        User user = getCurrentUser(authentication);
        requireHouseholdOwner(user);
        String householdId = getCurrentHouseholdId(authentication);
        holdServiceImpl.removeAppliancesBulk(householdId, request.ids());
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza remove my member.
     * @param authentication usuario autenticado.
     * @param memberUserId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Expulsar miembro de mi hogar (ADMIN u OWNER del hogar)")
    @ApiStandardDoc
    @DeleteMapping("/me/members/{memberUserId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Void> removeMyMember(Authentication authentication, @PathVariable String memberUserId) {
        String householdId = getCurrentHouseholdId(authentication);
        holdServiceImpl.removeMember(householdId, memberUserId);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza promote my member to owner.
     * @param authentication usuario autenticado.
     * @param userId el identificador del usuario
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Ascender miembro de mi hogar a OWNER (ADMIN u OWNER del hogar)")
    @ApiStandardDoc
    @PatchMapping("/me/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<UserResponse> promoteMyMemberToOwner(Authentication authentication, @PathVariable String userId) {
        String householdId = getCurrentHouseholdId(authentication);
        User promoted = holdServiceImpl.promoteToOwner(householdId, userId);
        return ResponseEntity.ok(userRestMapper.toResponse(promoted));
    }
    /**
     * Realiza join my user with invite.
     * @param authentication usuario autenticado.
     * @param token el token
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Unirme a un hogar con cAAaAaAaaAAaAAasAAdigo de invitaciAAaAaAaaAAaAAasAAn")
    @ApiPostDoc
    @PostMapping("/me/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> joinMyUserWithInvite(
            Authentication authentication,
            @RequestParam String token) {
        User currentUser = getCurrentUser(authentication);
        User updated = holdServiceImpl.addMemberByToken(token, currentUser.getId());
        return ResponseEntity.ok(userRestMapper.toResponse(updated));
    }
}




