package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;

import java.util.List;

public interface IFridgeItemService {
    /**
     * Recupera todos los items de todas las neveras.
     */
    List<FridgeItem> findAll();

    /**
     * Busca un item específico por su ID único.
     */
    FridgeItem findById(String id);

    /**
     * Lista todos los productos contenidos en una nevera específica.
     */
    List<FridgeItem> findByFridgeId(String fridgeId);

    /**
     * Registra un nuevo item en la nevera (ej. tras un escaneo de ticket).
     */
    FridgeItem create(FridgeItem fridgeItem);

    /**
     * Añade un producto a la nevera con validaciones.
     */
    FridgeItem addProductToFridge(String fridgeId, String productId, java.math.BigDecimal quantity,
            java.time.LocalDate expirationDate, ItemStatus initialStatus);

    /**
     * Consume parte de la cantidad de un item. Si tras el descuento la cantidad es cero, el ítem se elimina del inventario.
     */
    FridgeItem consumePartially(String itemId, java.math.BigDecimal quantityToConsume);

    /**
     * Da por consumido todo el ítem: lo elimina del inventario (no persiste cantidad 0).
     */
    void markAsConsumed(String itemId);

    /**
     * Recupera items próximos a caducar o caducados.
     */
    List<FridgeItem> getExpiringItems(String fridgeId, int daysThreshold);

    /**
     * Recupera el inventario filtrado por categoría.
     */
    List<FridgeItem> getInventoryByCategory(String fridgeId, String categoryId);

    /**
     * Actualiza un item existente (ideal para descontar unidades tras cocinar).
     */
    FridgeItem update(String id, FridgeItem fridgeItem);

    /**
     * Elimina un item (cuando se agota o se tira).
     */
    void delete(String id);
}