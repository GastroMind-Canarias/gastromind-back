package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.TicketServiceImpl;
import com.gastromind.api.application.usecases.ImportTicketFromImageUseCase;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Ticket", description = "Gestión de tickets de compra y registros de transacciones.")
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

    @Operation(summary = "Importar ticket desde imagen",
            description = "Extrae líneas e importes con Gemini (multimodal) y crea el ticket con productos "
                    + "(empareja por nombre o crea productos nuevos). "
                    + "Parámetro opcional store_id; si falta, se intenta emparejar por nombre de tienda leído en el ticket.")
    @ApiPostDoc
    @PostMapping(value = "/from-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketResponse> importFromImage(
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Usuario propietario del ticket")
            @RequestParam("user_id") String userId,
            @Parameter(description = "Tienda (opcional si el ticket muestra nombre y existe en catálogo)")
            @RequestParam(value = "store_id", required = false) String storeId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio");
        }
        if (file.getSize() > ticketImageProperties.getMaxImageBytes()) {
            throw new IllegalArgumentException(
                    "La imagen supera el tamaño máximo permitido (" + ticketImageProperties.getMaxImageBytes() + " bytes)");
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
        Ticket saved = importTicketFromImageUseCase.execute(bytes, contentType, userId, storeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toResponse(saved));
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