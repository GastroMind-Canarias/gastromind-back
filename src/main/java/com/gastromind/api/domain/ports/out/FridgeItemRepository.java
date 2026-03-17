package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;
import com.gastromind.api.domain.models.FridgeItem;

public interface FridgeItemRepository {
    FridgeItem save(FridgeItem fridgeItem);

    Optional<FridgeItem> findById(String id);

    List<FridgeItem> findByFridgeId(String fridgeId);

    void deleteById(String id);

    List<FridgeItem> findAll();

    List<FridgeItem> findExpiringItems(String fridgeId, java.time.LocalDate thresholdDate);

    List<FridgeItem> findByFridgeIdAndCategoryId(String fridgeId, String categoryId);
}