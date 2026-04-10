package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.application.usecases.ConsumeMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.CreateMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.DeleteMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.ListMyExpiringFridgeItemsUseCase;
import com.gastromind.api.application.usecases.ListMyFridgeItemsByCategoryUseCase;
import com.gastromind.api.application.usecases.ListMyFridgeItemsUseCase;
import com.gastromind.api.application.usecases.MarkMyFridgeItemConsumedUseCase;
import com.gastromind.api.application.usecases.UpdateMyFridgeItemUseCase;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.MyFridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.FridgeItemRestMapper;
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
@RequestMapping("/api/v1/fridge-items")
@Tag(name = "Items de Nevera", description = "Gestión de productos y stock en neveras. Rutas /me: Solo Owner y Admin. Rutas globales: Solo Admin.")
public class FridgeItemController {

    @Autowired
    private FridgeItemServiceImpl fridgeItemService;

    @Autowired
    FridgeItemRestMapper fridgeItemRestMapper;
    @Autowired
    private ListMyFridgeItemsUseCase listMyFridgeItemsUseCase;
    @Autowired
    private CreateMyFridgeItemUseCase createMyFridgeItemUseCase;
    @Autowired
    private UpdateMyFridgeItemUseCase updateMyFridgeItemUseCase;
    @Autowired
    private DeleteMyFridgeItemUseCase deleteMyFridgeItemUseCase;
    @Autowired
    private ConsumeMyFridgeItemUseCase consumeMyFridgeItemUseCase;
    @Autowired
    private MarkMyFridgeItemConsumedUseCase markMyFridgeItemConsumedUseCase;
    @Autowired
    private ListMyExpiringFridgeItemsUseCase listMyExpiringFridgeItemsUseCase;
    @Autowired
    private ListMyFridgeItemsByCategoryUseCase listMyFridgeItemsByCategoryUseCase;

    @Operation(summary = "Obtener todos los items (Solo Admin)", description = "Devuelve una lista global de todos los productos en todas las neveras.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getAll() {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.findAll()));
    }

    @Operation(summary = "Buscar item por ID (Solo Admin)", description = "Devuelve los detalles de un producto específico en la nevera.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> getById(
            @Parameter(description = "ID del item a buscar", example = "uuid-item-123") @PathVariable String id) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(fridgeItemService.findById(id)));
    }

    @Operation(summary = "Listar items de una nevera (Solo Admin)", description = "Devuelve todos los productos contenidos en una nevera específica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getByFridgeId(
            @Parameter(description = "ID de la nevera", example = "uuid-fridge-456") @PathVariable String fridgeId) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.findByFridgeId(fridgeId)));
    }

    @Operation(summary = "Listar items de mi nevera (Solo Owner y Admin)", description = "Devuelve los productos de la nevera del hogar del usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> listMyItems(Authentication authentication) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(listMyFridgeItemsUseCase.execute(authentication.getName())));
    }

    @Operation(summary = "Añadir item a la nevera (Solo Admin)", description = "Registra un nuevo producto o lote en el inventario.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> create(@Valid @RequestBody FridgeItemRequest fridgeItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                fridgeItemRestMapper.toResponse(fridgeItemService.addProductToFridge(
                        fridgeItem.fridgeId(),
                        fridgeItem.productId(),
                        fridgeItem.quantity(),
                        fridgeItem.expirationDate(),
                        fridgeItem.status())));
    }

    @Operation(summary = "Añadir item a mi nevera (Solo Owner y Admin)", description = "Registra un nuevo producto en la nevera del hogar autenticado. No incluye fridgeId: se toma de tu hogar.")
    @ApiPostDoc
    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<FridgeItemResponse> createMyItem(Authentication authentication,
            @Valid @RequestBody MyFridgeItemRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fridgeItemRestMapper.toResponse(
                createMyFridgeItemUseCase.execute(
                        authentication.getName(),
                        body.productId(),
                        body.quantity(),
                        body.expirationDate(),
                        body.status())));
    }

    @Operation(summary = "Actualizar stock de un item (Solo Admin)", description = "Modifica la cantidad o estado de un producto existente (ej. tras cocinar).")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> update(@PathVariable String id,
            @Valid @RequestBody FridgeItemRequest fridgeItem) {
        return ResponseEntity.ok(fridgeItemRestMapper
                .toResponse(fridgeItemService.update(id, fridgeItemRestMapper.toDomain(fridgeItem))));
    }

    @Operation(summary = "Actualizar item de mi nevera (Solo Owner y Admin)", description = "Modifica un producto de la nevera del hogar autenticado.")
    @ApiStandardDoc
    @PutMapping("/me/{itemId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<FridgeItemResponse> updateMyItem(Authentication authentication,
            @PathVariable String itemId,
            @Valid @RequestBody MyFridgeItemRequest body) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(updateMyFridgeItemUseCase.execute(
                authentication.getName(),
                itemId,
                fridgeItemRestMapper.toDomain(body))));
    }

    @Operation(summary = "Consumir parte de un item (Solo Admin)", description = "Descuenta una cantidad del stock. Si el stock llega a cero, el ítem se elimina del inventario.")
    @ApiStandardDoc
    @PutMapping("/{id}/consume")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> consumePartially(
            @PathVariable String id,
            @Parameter(description = "Cantidad a consumir", example = "0.5") @RequestBody java.math.BigDecimal quantity) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(fridgeItemService.consumePartially(id, quantity)));
    }

    @Operation(summary = "Consumir parte de un item de mi nevera (Solo Owner y Admin)", description = "Descuenta cantidad del stock; si queda en cero, el ítem se elimina del inventario.")
    @ApiStandardDoc
    @PutMapping("/me/{itemId}/consume")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<FridgeItemResponse> consumePartiallyMyItem(Authentication authentication,
            @PathVariable String itemId,
            @RequestBody java.math.BigDecimal quantity) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(
                consumeMyFridgeItemUseCase.execute(authentication.getName(), itemId, quantity)));
    }

    @Operation(summary = "Marcar como consumido (Solo Admin)", description = "Elimina el ítem del inventario (equivalente a haberlo consumido por completo).")
    @ApiStandardDoc
    @PutMapping("/{id}/mark-consumed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markAsConsumed(@PathVariable String id) {
        fridgeItemService.markAsConsumed(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar item de mi nevera como consumido (Solo Owner y Admin)", description = "Quita el ítem del inventario de tu nevera (consumido por completo).")
    @ApiStandardDoc
    @PutMapping("/me/{itemId}/mark-consumed")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> markMyItemAsConsumed(Authentication authentication, @PathVariable String itemId) {
        markMyFridgeItemConsumedUseCase.execute(authentication.getName(), itemId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar items por caducar (Solo Admin)", description = "Recupera los productos próximos a caducar para una nevera específica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}/expiring")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getExpiring(
            @PathVariable String fridgeId,
            @Parameter(description = "Número de días de antelación", example = "5") @org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        return ResponseEntity
                .ok(fridgeItemRestMapper.toResponseList(fridgeItemService.getExpiringItems(fridgeId, days)));
    }

    @Operation(summary = "Listar mis items por caducar (Solo Owner y Admin)", description = "Recupera productos próximos a caducar para la nevera del hogar autenticado.")
    @ApiStandardDoc
    @GetMapping("/me/expiring")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getMyExpiring(
            Authentication authentication,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(
                listMyExpiringFridgeItemsUseCase.execute(authentication.getName(), days)));
    }

    @Operation(summary = "Filtrar por categoría (Solo Admin)", description = "Devuelve el inventario de una nevera filtrado por una categoría de producto específica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}/category/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getByCategory(@PathVariable String fridgeId,
            @PathVariable String categoryId) {
        return ResponseEntity.ok(
                fridgeItemRestMapper.toResponseList(fridgeItemService.getInventoryByCategory(fridgeId, categoryId)));
    }

    @Operation(summary = "Filtrar mi inventario por categoría (Solo Owner y Admin)", description = "Devuelve el inventario de la nevera autenticada filtrado por categoría.")
    @ApiStandardDoc
    @GetMapping("/me/category/{categoryId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getMyByCategory(Authentication authentication,
            @PathVariable String categoryId) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(
                listMyFridgeItemsByCategoryUseCase.execute(authentication.getName(), categoryId)));
    }

    @Operation(summary = "Eliminar item (Solo Admin)", description = "Borra un producto del inventario (consumido o desechado).")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        fridgeItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar item de mi nevera (Solo Owner y Admin)", description = "Borra un producto del inventario de la nevera autenticada.")
    @ApiStandardDoc
    @DeleteMapping("/me/{itemId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> deleteMyItem(Authentication authentication, @PathVariable String itemId) {
        deleteMyFridgeItemUseCase.execute(authentication.getName(), itemId);
        return ResponseEntity.noContent().build();
    }
}