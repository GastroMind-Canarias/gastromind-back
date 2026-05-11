package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.UsualPurchase;

import java.util.List;
import java.util.Optional;

/**
 * Patrones de recompra por usuario ligados al catálogo de productos.
 */
public interface UsualPurchaseRepository {

    UsualPurchase save(UsualPurchase usualPurchase);

    Optional<UsualPurchase> findById(String id);

    void deleteById(String id);

    List<UsualPurchase> findAll();

    List<UsualPurchase> findAllByUserId(String userId);

    Optional<UsualPurchase> findByUserIdAndProductId(String userId, String productId);
}
