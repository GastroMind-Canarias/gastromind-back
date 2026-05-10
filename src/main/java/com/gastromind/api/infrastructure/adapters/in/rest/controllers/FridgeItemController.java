package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.application.usecases.ConsumeMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.ConsumeMyFridgeItemsBatchUseCase;
import com.gastromind.api.application.usecases.CreateMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.DeleteMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.ListMyExpiringFridgeItemsUseCase;
import com.gastromind.api.application.usecases.ListMyFridgeItemsByCategoryUseCase;
import com.gastromind.api.application.usecases.ListMyFridgeItemsUseCase;
import com.gastromind.api.application.usecases.MarkMyFridgeItemConsumedUseCase;
import com.gastromind.api.application.usecases.UpdateMyFridgeItemUseCase;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.domain.models.FridgeItemConsumeLine;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemConsumeBatchRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.MyFridgeItemBatchRequest;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fridge-items")
@Tag(name = "Items de Nevera", description = "Gestion de productos y stock en neveras. Rutas /me: Owner, Member y Admin. Rutas globales: Solo Admin.")
/**
 * Controlador REST para operaciones de fridge item.
 */
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
    private ConsumeMyFridgeItemsBatchUseCase consumeMyFridgeItemsBatchUseCase;
    @Autowired
    private MarkMyFridgeItemConsumedUseCase markMyFridgeItemConsumedUseCase;
    @Autowired
    private ListMyExpiringFridgeItemsUseCase listMyExpiringFridgeItemsUseCase;
    @Autowired
    private ListMyFridgeItemsByCategoryUseCase listMyFridgeItemsByCategoryUseCase;
    /**
     * Lista todos los fridge item.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Obtener todos los items (Solo Admin)", description = "Devuelve una lista global de todos los productos en todas las neveras.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getAll() {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.findAll()));
    }
    /**
     * Devuelve fridge item por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Buscar item por ID (Solo Admin)", description = "Devuelve los detalles de un producto especifico en la nevera.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> getById(
            @Parameter(description = "ID del item a buscar", example = "uuid-item-123") @PathVariable String id) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(fridgeItemService.findById(id)));
    }
    /**
     * Devuelve fridge item por fridge id.
     * @param fridgeId identificador de la nevera.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar items de una nevera (Solo Admin)", description = "Devuelve todos los productos contenidos en una nevera especifica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getByFridgeId(
            @Parameter(description = "ID de la nevera", example = "uuid-fridge-456") @PathVariable String fridgeId) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.findByFridgeId(fridgeId)));
    }
    /**
     * Realiza list my items.
     * @param authentication usuario autenticado.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar items de mi nevera (Owner, Member y Admin)", description = "Devuelve los productos de la nevera del hogar del usuario autenticado.")
    @ApiStandardDoc
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> listMyItems(Authentication authentication) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(listMyFridgeItemsUseCase.execute(authentication.getName())));
    }
    /**
     * Registra un nuevo fridge item.
     * @param fridgeItem el producto de la nevera
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Anadir item a la nevera (Solo Admin)", description = "Registra un nuevo producto o lote en el inventario.")
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
    /**
     * Realiza create my item.
     * @param authentication usuario autenticado.
     * @param body datos enviados en el cuerpo de la solicitud.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Anadir item a mi nevera (Owner, Member y Admin)", description = "Registra un nuevo producto en la nevera del hogar autenticado. No incluye fridgeId: se toma de tu hogar.")
    @ApiPostDoc
    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<FridgeItemResponse> createMyItem(Authentication authentication,
            @Valid @RequestBody MyFridgeItemRequest body) {
        validateCreateMyItemBody(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(fridgeItemRestMapper.toResponse(
                createMyFridgeItemUseCase.execute(
                        authentication.getName(),
                        body.productId(),
                        body.productName(),
                        body.quantity(),
                        body.expirationDate(),
                        body.status())));
    }

    @Operation(summary = "Anadir items a mi nevera por lote (Owner, Member y Admin)", description = "Registra multiples productos en la nevera del hogar autenticado.")
    @ApiPostDoc
    @PostMapping("/me/batch")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> createMyItemsBatch(Authentication authentication,
            @Valid @RequestBody MyFridgeItemBatchRequest body) {
        List<FridgeItemResponse> created = body.items().stream()
                .peek(this::validateCreateMyItemBody)
                .map(item -> createMyFridgeItemUseCase.execute(
                        authentication.getName(),
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.expirationDate(),
                        item.status()))
                .map(fridgeItemRestMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /**
     * Define un fridge item existente.
     * @param id el identificador del recurso
     * @param fridgeItem el producto de la nevera
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Actualizar stock de un item (Solo Admin)", description = "Modifica la cantidad o estado de un producto existente (ej. tras cocinar).")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> update(@PathVariable String id,
            @Valid @RequestBody FridgeItemRequest fridgeItem) {
        return ResponseEntity.ok(fridgeItemRestMapper
                .toResponse(fridgeItemService.update(id, fridgeItemRestMapper.toDomain(fridgeItem))));
    }
    /**
     * Variante de actualizacion acotada al inventario de tu hogar. No hace falta repetir el
     * producto del catalogo: el item ya lo tiene guardado; solo envia lo que quieres cambiar
     * (cantidad, caducidad, estado).
     */

    @Operation(summary = "Actualizar item de mi nevera (Owner, Member y Admin)", description = "Modifica cantidad, caducidad y estado del item. El producto del catalogo queda asociado al item; no hace falta enviar productId.")
    @ApiStandardDoc
    @PutMapping("/me/{itemId}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<FridgeItemResponse> updateMyItem(Authentication authentication,
            @PathVariable String itemId,
            @Valid @RequestBody MyFridgeItemRequest body) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(updateMyFridgeItemUseCase.execute(
                authentication.getName(),
                itemId,
                fridgeItemRestMapper.toDomain(body))));
    }
    /**
     * Realiza consume partially.
     * @param id el identificador del recurso
     * @param quantity la cantidad
     * @return resultado de la operacion solicitada.
     */

    /**
     * Descuenta stock en bloque (admin). Sirve para registrar varios usos sin viajes extra al servidor.
     */
    @Operation(summary = "Consumir parte de varios items (Solo Admin)", description = "Varios descuentos en una peticion; la respuesta sigue el orden del body. Si una linea falla, no se aplica ninguna.")
    @ApiStandardDoc
    @PutMapping("/batch/consume")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> consumePartiallyBatch(
            @Valid @RequestBody FridgeItemConsumeBatchRequest body) {
        List<FridgeItemConsumeLine> lines = body.items().stream()
                .map(line -> new FridgeItemConsumeLine(line.itemId(), line.quantity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(fridgeItemService.consumePartiallyBatch(lines)));
    }

    @Operation(summary = "Consumir parte de un item (Solo Admin)", description = "Descuenta una cantidad del stock. Si el stock llega a cero, el item se elimina del inventario.")
    @ApiStandardDoc
    @PutMapping("/{id}/consume")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FridgeItemResponse> consumePartially(
            @PathVariable String id,
            @Parameter(description = "Cantidad a consumir", example = "0.5") @RequestBody java.math.BigDecimal quantity) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(fridgeItemService.consumePartially(id, quantity)));
    }
    /**
     * Realiza consume partially my item.
     * @param authentication usuario autenticado.
     * @param itemId identificador del item.
     * @param quantity la cantidad
     * @return resultado de la operacion solicitada.
     */

    /**
     * Igual que consumir una linea en /me, pero en lista; solo items de tu nevera.
     */
    @Operation(summary = "Consumir parte de varios items de mi nevera (Owner, Member y Admin)", description = "Mismo criterio que el consume unitario /me, pero varias lineas; todo o nada si algo falla.")
    @ApiStandardDoc
    @PutMapping("/me/batch/consume")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> consumePartiallyMyItemsBatch(Authentication authentication,
            @Valid @RequestBody FridgeItemConsumeBatchRequest body) {
        List<FridgeItemConsumeLine> lines = body.items().stream()
                .map(line -> new FridgeItemConsumeLine(line.itemId(), line.quantity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(
                consumeMyFridgeItemsBatchUseCase.execute(authentication.getName(), lines)));
    }

    @Operation(summary = "Consumir parte de un item de mi nevera (Owner, Member y Admin)", description = "Descuenta cantidad del stock; si queda en cero, el item se elimina del inventario.")
    @ApiStandardDoc
    @PutMapping("/me/{itemId}/consume")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<FridgeItemResponse> consumePartiallyMyItem(Authentication authentication,
            @PathVariable String itemId,
            @RequestBody java.math.BigDecimal quantity) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponse(
                consumeMyFridgeItemUseCase.execute(authentication.getName(), itemId, quantity)));
    }
    /**
     * Realiza mark as consumed.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Marcar como consumido (Solo Admin)", description = "Elimina el item del inventario (equivalente a haberlo consumido por completo).")
    @ApiStandardDoc
    @PutMapping("/{id}/mark-consumed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markAsConsumed(@PathVariable String id) {
        fridgeItemService.markAsConsumed(id);
        return ResponseEntity.ok().build();
    }
    /**
     * Realiza mark my item as consumed.
     * @param authentication usuario autenticado.
     * @param itemId identificador del item.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Marcar item de mi nevera como consumido (Owner, Member y Admin)", description = "Quita el item del inventario de tu nevera (consumido por completo).")
    @ApiStandardDoc
    @PutMapping("/me/{itemId}/mark-consumed")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<Void> markMyItemAsConsumed(Authentication authentication, @PathVariable String itemId) {
        markMyFridgeItemConsumedUseCase.execute(authentication.getName(), itemId);
        return ResponseEntity.ok().build();
    }
    /**
     * Devuelve fridge item por expiring.
     * @param fridgeId identificador de la nevera.
     * @param days dias de anticipacion para filtrar caducidades.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar items por caducar (Solo Admin)", description = "Recupera los productos proximos a caducar para una nevera especifica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}/expiring")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getExpiring(
            @PathVariable String fridgeId,
            @Parameter(description = "Numero de dias de antelacion", example = "5") @org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        return ResponseEntity
                .ok(fridgeItemRestMapper.toResponseList(fridgeItemService.getExpiringItems(fridgeId, days)));
    }
    /**
     * Devuelve fridge item por my expiring.
     * @param authentication usuario autenticado.
     * @param days dias de anticipacion para filtrar caducidades.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Listar mis items por caducar (Owner, Member y Admin)", description = "Recupera productos proximos a caducar para la nevera del hogar autenticado.")
    @ApiStandardDoc
    @GetMapping("/me/expiring")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getMyExpiring(
            Authentication authentication,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(
                listMyExpiringFridgeItemsUseCase.execute(authentication.getName(), days)));
    }
    /**
     * Devuelve fridge item por category.
     * @param fridgeId identificador de la nevera.
     * @param categoryId identificador de la categoria.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Filtrar por categoria (Solo Admin)", description = "Devuelve el inventario de una nevera filtrado por una categoria de producto especifica.")
    @ApiStandardDoc
    @GetMapping("/fridge/{fridgeId}/category/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getByCategory(@PathVariable String fridgeId,
            @PathVariable String categoryId) {
        return ResponseEntity.ok(
                fridgeItemRestMapper.toResponseList(fridgeItemService.getInventoryByCategory(fridgeId, categoryId)));
    }
    /**
     * Devuelve fridge item por my by category.
     * @param authentication usuario autenticado.
     * @param categoryId identificador de la categoria.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Filtrar mi inventario por categoria (Owner, Member y Admin)", description = "Devuelve el inventario de la nevera autenticada filtrado por categoria.")
    @ApiStandardDoc
    @GetMapping("/me/category/{categoryId}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<List<FridgeItemResponse>> getMyByCategory(Authentication authentication,
            @PathVariable String categoryId) {
        return ResponseEntity.ok(fridgeItemRestMapper.toResponseList(
                listMyFridgeItemsByCategoryUseCase.execute(authentication.getName(), categoryId)));
    }
    /**
     * Elimina un fridge item.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar item (Solo Admin)", description = "Borra un producto del inventario (consumido o desechado).")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        fridgeItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Realiza delete my item.
     * @param authentication usuario autenticado.
     * @param itemId identificador del item.
     * @return resultado de la operacion solicitada.
     */

    @Operation(summary = "Eliminar item de mi nevera (Owner, Member y Admin)", description = "Borra un producto del inventario de la nevera autenticada.")
    @ApiStandardDoc
    @DeleteMapping("/me/{itemId}")
    @PreAuthorize("hasAnyRole('OWNER','MEMBER','ADMIN')")
    public ResponseEntity<Void> deleteMyItem(Authentication authentication, @PathVariable String itemId) {
        deleteMyFridgeItemUseCase.execute(authentication.getName(), itemId);
        return ResponseEntity.noContent().build();
    }

    private void validateCreateMyItemBody(MyFridgeItemRequest body) {
        if ((body.productId() == null || body.productId().trim().isEmpty())
                && (body.productName() == null || body.productName().trim().isEmpty())) {
            throw new IllegalArgumentException("Debes indicar productId o productName");
        }
    }
}




