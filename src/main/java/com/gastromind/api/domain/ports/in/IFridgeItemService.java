package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;

import java.util.List;

/**
 * Define las operaciones de negocio para ítems de nevera.
 */
public interface IFridgeItemService {
    List<FridgeItem> findAll();

    FridgeItem findById(String id);

    List<FridgeItem> findByFridgeId(String fridgeId);

    FridgeItem create(FridgeItem fridgeItem);

    FridgeItem addProductToFridge(String fridgeId, String productId, java.math.BigDecimal quantity,
            java.time.LocalDate expirationDate, ItemStatus initialStatus);

    FridgeItem addLabeledItemToFridge(String fridgeId, String productLabel, java.math.BigDecimal quantity,
            java.time.LocalDate expirationDate, ItemStatus initialStatus);

    FridgeItem consumePartially(String itemId, java.math.BigDecimal quantityToConsume);

    void markAsConsumed(String itemId);

    List<FridgeItem> getExpiringItems(String fridgeId, int daysThreshold);

    List<FridgeItem> getInventoryByCategory(String fridgeId, String categoryId);

    FridgeItem update(String id, FridgeItem fridgeItem);

    void delete(String id);
}
