package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.application.services.TicketServiceImpl;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.TicketRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Ticket", description = "Gestión de tickets de compra y registros de transacciones.")
public class TicketController {

    @Autowired
    private TicketServiceImpl ticketServiceImpl;

    @Autowired
    private TicketRestMapper ticketMapper;

    @Operation(summary = "Obtener todos los tickets", description = "Devuelve una lista completa de todos los tickets registrados.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAll() {
        List<Ticket> tickets = ticketServiceImpl.findAll();
        return ResponseEntity.ok(ticketMapper.toResponseList(tickets));
    }

    @Operation(summary = "Buscar ticket por ID", description = "Devuelve un único ticket basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(
            @Parameter(description = "ID del ticket a buscar", example = "1")
            @PathVariable String id) {
        Ticket ticket = ticketServiceImpl.findById(id);
        return ResponseEntity.ok(ticketMapper.toResponse(ticket));
    }

    @Operation(summary = "Crear nuevo ticket", description = "Registra un nuevo ticket en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
        Ticket ticketDomain = ticketMapper.toDomain(request);
        Ticket savedTicket = ticketServiceImpl.create(ticketDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponse(savedTicket));
    }

    @Operation(summary = "Actualizar ticket", description = "Modifica los datos de un ticket existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> update(@PathVariable String id, @Valid @RequestBody TicketRequest request) {
        Ticket ticketDomain = ticketMapper.toDomain(request);
        Ticket updatedTicket = ticketServiceImpl.update(id, ticketDomain);
        return ResponseEntity.ok(ticketMapper.toResponse(updatedTicket));
    }

    @Operation(summary = "Eliminar ticket", description = "Borra físicamente un ticket de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ticketServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}