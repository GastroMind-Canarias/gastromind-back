package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.FridgeItem;

import java.util.List;
import java.util.Optional;

/**
 * Líneas de inventario en nevera: cantidades, caducidad y vínculo al producto del catálogo.
 */
public interface FridgeItemRepository {
    FridgeItem save(FridgeItem fridgeItem);

    Optional<FridgeItem> findById(String id);

    List<FridgeItem> findByFridgeId(String fridgeId);

    void deleteById(String id);

    List<FridgeItem> findAll();

    List<FridgeItem> findExpiringItems(String fridgeId, java.time.LocalDate thresholdDate);

    List<FridgeItem> findByFridgeIdAndCategoryId(String fridgeId, String categoryId);
}
