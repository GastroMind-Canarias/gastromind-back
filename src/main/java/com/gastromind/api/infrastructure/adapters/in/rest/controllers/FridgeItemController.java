package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.FridgeItemRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fridge-items")
@Tag(name = "Items de Nevera", description = "Gestión granular de productos y stock dentro de las neveras.")
public class FridgeItemController {

    @Autowired
    private FridgeItemServiceImpl fridgeItemService;

    @Autowired
    FridgeItemRestMapper fridgeItemRestMapper;

    @Operation(summary = "Obtener todos los items", description = "Devuelve una lista global de todos los productos en todas las neveras.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<FridgeItemResponse>> getAll() {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.findAll()));
    }

    @Operation(summary = "Buscar item por ID", description = "Devuelve los detalles de un producto específico en la nevera.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<FridgeItemResponse> getById(
            @Parameter(description = "ID del item a buscar", example = "uuid-item-123") @PathVariable String id) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(fridgeItemService.findById(id)));
    }

    @Operation(summary = "Listar items de una nevera", description = "Devuelve todos los productos contenidos en una nevera específica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}")
    public ResponseEntity<List<FridgeItemResponse>> getByFridgeId(
            @Parameter(description = "ID de la nevera", example = "uuid-fridge-456") @PathVariable String fridgeId) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.findByFridgeId(fridgeId)));
    }

    @Operation(summary = "Añadir item a la nevera", description = "Registra un nuevo producto o lote en el inventario.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<FridgeItemResponse> create(@RequestBody FridgeItemRequest fridgeItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                fridgeItemRestMapper.toResponse(fridgeItemService.addProductToFridge(
                        fridgeItem.fridgeId(),
                        fridgeItem.productId(),
                        fridgeItem.quantity(),
                        fridgeItem.expirationDate())));
    }

    @Operation(summary = "Actualizar stock de un item", description = "Modifica la cantidad o estado de un producto existente (ej. tras cocinar).")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<FridgeItemResponse> update(@PathVariable String id,
            @RequestBody FridgeItemRequest fridgeItem) {
        return ResponseEntity.ok(fridgeItemRestMapper
                .toResponse(fridgeItemService.update(id, fridgeItemRestMapper.toDomain(fridgeItem))));
    }

    @Operation(summary = "Consumir parte de un item", description = "Descuenta una cantidad específica del stock disponible.")
    @ApiStandardDoc
    @PutMapping("/{id}/consume")
    public ResponseEntity<FridgeItemResponse> consumePartially(
            @PathVariable String id,
            @Parameter(description = "Cantidad a consumir", example = "0.5") @RequestBody java.math.BigDecimal quantity) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(fridgeItemService.consumePartially(id, quantity)));
    }

    @Operation(summary = "Marcar como consumido", description = "Marca un item con estado consumido y cantidad cero.")
    @ApiStandardDoc
    @PutMapping("/{id}/mark-consumed")
    public ResponseEntity<Void> markAsConsumed(@PathVariable String id) {
        fridgeItemService.markAsConsumed(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar items por caducar", description = "Recupera los productos próximos a caducar para una nevera específica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}/expiring")
    public ResponseEntity<List<FridgeItemResponse>> getExpiring(
            @PathVariable String fridgeId,
            @Parameter(description = "Número de días de antelación", example = "5") @org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        return ResponseEntity
                .ok(fridgeItemRestMapper.toResponseList(fridgeItemService.getExpiringItems(fridgeId, days)));
    }

    @Operation(summary = "Filtrar por categoría", description = "Devuelve el inventario de una nevera filtrado por una categoría de producto específica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}/category/{categoryId}")
    public ResponseEntity<List<FridgeItemResponse>> getByCategory(@PathVariable String fridgeId,
            @PathVariable String categoryId) {
        return ResponseEntity.ok(
                fridgeItemRestMapper.toResponseList(fridgeItemService.getInventoryByCategory(fridgeId, categoryId)));
    }

    @Operation(summary = "Eliminar item", description = "Borra un producto del inventario (consumido o desechado).")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        fridgeItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}