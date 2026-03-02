package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.UsualPurchase;

public interface UsualPurchaseRepository {

    UsualPurchase save(UsualPurchase usualPurchase);

    Optional<UsualPurchase> findById(String id);

    void deleteById(String id);

    List<UsualPurchase> findAll();

    /**
     * Devuelve las compras habituales de un usuario ordenadas por frecuencia
     * descendente
     */
    List<UsualPurchase> findByUserIdOrderByFrequencyDesc(String userId);

    /** Busca una compra habitual concreta por userId y productId (para upsert) */
    Optional<UsualPurchase> findByUserIdAndProductId(String userId, String productId);
}
