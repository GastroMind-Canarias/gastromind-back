package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.TicketServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.ImportTicketFromImageUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.TicketRestMapper;
import com.gastromind.api.infrastructure.adapters.out.ai.TicketImageProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Ticket", description = "GestiAAaAaAaaAAaAAasAAn de tickets de compra y registros de transacciones.")
/**
 * Controlador REST para operaciones de ticket.
 */
public class TicketController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            "image/jpg",
            MediaType.IMAGE_PNG_VALUE,
            "image/webp");

    @Autowired
    private TicketServiceImpl ticketServiceImpl;

    @Autowired
    private TicketRestMapper ticketMapper;

    @Autowired
    private ImportTicketFromImageUseCase importTicketFromImageUseCase;

    @Autowired
    private TicketImageProperties ticketImageProperties;

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
    /**
     * Lista todos los ticket.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Obtener todos los tickets (solo ADMIN)", description = "Lista completa de tickets en el sistema.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponse>> getAll() {
        List<Ticket> tickets = ticketServiceImpl.findAll();
        return ResponseEntity.ok(ticketMapper.toResponseList(tickets));
    }
    /**
     * Realiza list my tickets.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar tickets del hogar", description = "Tickets del hogar del usuario (incluye los registrados por otros miembros).")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<TicketResponse>> listMyTickets(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(ticketMapper.toResponseList(ticketServiceImpl.findAllVisibleForUserHousehold(user.getId())));
    }
    /**
     * Devuelve ticket por my ticket by id.
     * @param authentication usuario autenticado.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Obtener ticket por ID (hogar)", description = "Si el ticket pertenece al mismo hogar que el usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<TicketResponse> getMyTicketById(Authentication authentication, @PathVariable String id) {
        User user = getCurrentUser(authentication);
        Ticket ticket = ticketServiceImpl.findByIdForHouseholdMember(id, user.getId());
        return ResponseEntity.ok(ticketMapper.toResponse(ticket));
    }
    /**
     * Realiza create my ticket.
     * @param authentication usuario autenticado.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Crear ticket para mi usuario", description = "Asocia el ticket al usuario autenticado.")
    @ApiPostDoc
    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<TicketResponse> createMyTicket(
            Authentication authentication,
            @Valid @RequestBody TicketMeRequest request) {
        User user = getCurrentUser(authentication);
        Ticket ticketDomain = ticketMapper.toDomainForMe(request, user.getId());
        Ticket savedTicket = ticketServiceImpl.create(ticketDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponse(savedTicket));
    }

    @Operation(summary = "Importar ticket desde imagen (usuario autenticado)",
            description = "Extrae lAAaAaAaaAAaAAasAAneas e importes con Gemini y crea el ticket para el usuario actual. "
                    + "ParAAaAaAaaAAaAAasAAmetro opcional store_id.")
    /**
     * Realiza import from image.
     * @param authentication usuario autenticado.
     * @param file valor a utilizar.
     * @param storeId identificador de la tienda (opcional).
     * @return resultado de la operacion solicitada.
     */
    @ApiPostDoc
    @PostMapping(value = "/from-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<TicketResponse> importFromImage(
            Authentication authentication,
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Tienda (opcional si el ticket muestra nombre y existe en catAAaAaAaaAAaAAasAAlogo)")
            @RequestParam(value = "store_id", required = false) String storeId) {
        User user = getCurrentUser(authentication);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio");
        }
        if (file.getSize() > ticketImageProperties.getMaxImageBytes()) {
            throw new IllegalArgumentException(
                    "La imagen supera el tamaAAaAaAaaAAaAAasAAo mAAaAaAaaAAaAAasAAximo permitido (" + ticketImageProperties.getMaxImageBytes() + " bytes)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Tipo de imagen no permitido. Use JPEG, PNG o WebP.");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo de imagen", e);
        }
        Ticket saved = importTicketFromImageUseCase.execute(bytes, contentType, user.getId(), storeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponse(saved));
    }
    /**
     * Registra un nuevo ticket.
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Crear ticket (solo ADMIN)", description = "Registra un ticket con user_id explAAaAaAaaAAaAAasAAcito en el cuerpo.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
        Ticket ticketDomain = ticketMapper.toDomain(request);
        Ticket savedTicket = ticketServiceImpl.create(ticketDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponse(savedTicket));
    }
    /**
     * Devuelve ticket por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Buscar ticket por ID (solo ADMIN)", description = "Detalle de un ticket por identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> getById(
            @Parameter(description = "ID del ticket a buscar", example = "1")
            @PathVariable String id) {
        Ticket ticket = ticketServiceImpl.findById(id);
        return ResponseEntity.ok(ticketMapper.toResponse(ticket));
    }
    /**
     * Define un ticket existente.
     * @param id el identificador del recurso
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Actualizar ticket (solo ADMIN)", description = "Modifica un ticket existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> update(@PathVariable String id, @Valid @RequestBody TicketRequest request) {
        Ticket ticketDomain = ticketMapper.toDomain(request);
        Ticket updatedTicket = ticketServiceImpl.update(id, ticketDomain);
        return ResponseEntity.ok(ticketMapper.toResponse(updatedTicket));
    }
    /**
     * Realiza update my ticket.
     * @param authentication usuario autenticado.
     * @param id el identificador del recurso
     * @param request los datos de la solicitud
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Actualizar uno de mis tickets", description = "Solo si el ticket pertenece al usuario autenticado.")
    @ApiStandardDoc
    @PutMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<TicketResponse> updateMyTicket(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody TicketMeRequest request) {
        User user = getCurrentUser(authentication);
        Ticket ticketDomain = ticketMapper.toDomainForMe(request, user.getId());
        Ticket updatedTicket = ticketServiceImpl.updateForUser(id, ticketDomain, user.getId());
        return ResponseEntity.ok(ticketMapper.toResponse(updatedTicket));
    }
    /**
     * Elimina un ticket.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar ticket (solo ADMIN)", description = "Borra un ticket de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ticketServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza delete my ticket.
     * @param authentication usuario autenticado.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar uno de mis tickets", description = "Solo si el ticket pertenece al usuario autenticado.")
    @ApiStandardDoc
    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<Void> deleteMyTicket(Authentication authentication, @PathVariable String id) {
        User user = getCurrentUser(authentication);
        ticketServiceImpl.deleteForUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}




