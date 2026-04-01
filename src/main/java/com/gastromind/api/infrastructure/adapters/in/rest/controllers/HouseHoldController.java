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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/households")
@Tag(name = "Unidad Familiar")
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
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }
        return currentUser.getHouseHold_id().getId();
    }

    @Operation(summary = "Listar todos los hogares (Solo ADMIN)")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HouseHoldResponse>> getAll() {
        return ResponseEntity.ok(houseHoldMapper.toResponseList(holdServiceImpl.findAll()));
    }

    @Operation(summary = "Eliminar unidad familiar (ADMIN/OWNER)")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        holdServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Añadir dispositivo (ADMIN/OWNER)")
    @ApiPostDoc
    @PostMapping("/{id}/appliances")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplianceResponse> addAppliance(@PathVariable String id, @RequestParam Appliance appliance) {
        HouseholdAppliance saved = holdServiceImpl.addAppliance(id, appliance);
        return ResponseEntity.status(HttpStatus.CREATED).body(applianceRestMapper.toResponse(saved));
    }

    @Operation(summary = "Expulsar miembro (ADMIN/OWNER)")
    @ApiStandardDoc
    @DeleteMapping("/{id}/members/{memberUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeMember(@PathVariable String id, @PathVariable String memberUserId) {
        holdServiceImpl.removeMember(id, memberUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Generar token de invitación")
    @ApiStandardDoc
    @PostMapping("/{id}/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> invite(@PathVariable String id) {
        return ResponseEntity.ok(holdServiceImpl.generateInviteToken(id));
    }

    @Operation(summary = "Ascender a OWNER (ADMIN/OWNER)")
    @ApiStandardDoc
    @PatchMapping("/{id}/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> promoteToOwner(@PathVariable String id, @PathVariable String userId) {
        User promoted = holdServiceImpl.promoteToOwner(id, userId);
        return ResponseEntity.ok(userRestMapper.toResponse(promoted));
    }

    @Operation(summary = "Ver detalle del hogar (ADMIN/OWNER)")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HouseHoldResponse> getById(@PathVariable String id) {
        HouseHold houseHold = holdServiceImpl.findById(id);
        return ResponseEntity.ok(houseHoldMapper.toResponse(houseHold));
    }

    @Operation(summary = "Listar miembros del hogar")
    @ApiStandardDoc
    @GetMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> listMembers(@PathVariable String id) {
        List<User> members = holdServiceImpl.listMembers(id);
        return ResponseEntity.ok(userRestMapper.toResponseList(members));
    }

    @Operation(summary = "Crear nuevo hogar")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HouseHoldResponse> create(@Valid @RequestBody HouseHoldRequest request) {
        HouseHold saved = holdServiceImpl.create(houseHoldMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(houseHoldMapper.toResponse(saved));
    }

    @Operation(summary = "Abandonar el hogar actual")
    @ApiPostDoc
    @PostMapping("/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> leave(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        holdServiceImpl.leaveHousehold(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ver detalle de mi hogar")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HouseHoldResponse> getMyHousehold(Authentication authentication) {
        String householdId = getCurrentHouseholdId(authentication);
        HouseHold houseHold = holdServiceImpl.findById(householdId);
        return ResponseEntity.ok(houseHoldMapper.toResponse(houseHold));
    }

    @Operation(summary = "Listar miembros de mi hogar")
    @ApiStandardDoc
    @GetMapping("/me/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> listMyMembers(Authentication authentication) {
        String householdId = getCurrentHouseholdId(authentication);
        List<User> members = holdServiceImpl.listMembers(householdId);
        return ResponseEntity.ok(userRestMapper.toResponseList(members));
    }

    @Operation(summary = "Generar token de invitación de mi hogar")
    @ApiStandardDoc
    @PostMapping("/me/invite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> inviteMyHousehold(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser.getRole() != Role.ROLE_OWNER && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el owner puede generar invitaciones");
        }
        String householdId = getCurrentHouseholdId(authentication);
        return ResponseEntity.ok(holdServiceImpl.generateInviteToken(householdId));
    }

    @Operation(summary = "Añadir dispositivo a mi hogar")
    @ApiPostDoc
    @PostMapping("/me/appliances")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplianceResponse> addMyAppliance(
            Authentication authentication,
            @RequestParam Appliance appliance) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser.getRole() != Role.ROLE_OWNER && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el owner puede añadir dispositivos");
        }
        String householdId = getCurrentHouseholdId(authentication);
        HouseholdAppliance saved = holdServiceImpl.addAppliance(householdId, appliance);
        return ResponseEntity.status(HttpStatus.CREATED).body(applianceRestMapper.toResponse(saved));
    }

    @Operation(summary = "Expulsar miembro de mi hogar")
    @ApiStandardDoc
    @DeleteMapping("/me/members/{memberUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeMyMember(Authentication authentication, @PathVariable String memberUserId) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser.getRole() != Role.ROLE_OWNER && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el owner puede expulsar miembros");
        }
        String householdId = getCurrentHouseholdId(authentication);
        holdServiceImpl.removeMember(householdId, memberUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ascender miembro de mi hogar a OWNER")
    @ApiStandardDoc
    @PatchMapping("/me/promote/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> promoteMyMemberToOwner(Authentication authentication, @PathVariable String userId) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser.getRole() != Role.ROLE_OWNER && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el owner puede ascender miembros");
        }
        String householdId = getCurrentHouseholdId(authentication);
        User promoted = holdServiceImpl.promoteToOwner(householdId, userId);
        return ResponseEntity.ok(userRestMapper.toResponse(promoted));
    }

    @Operation(summary = "Unirme a un hogar con código de invitación")
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