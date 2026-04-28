package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.UsualPurchase;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para usual purchase.
 */
public interface UsualPurchaseRepository {

    UsualPurchase save(UsualPurchase usualPurchase);

    Optional<UsualPurchase> findById(String id);

    void deleteById(String id);

    List<UsualPurchase> findAll();

    List<UsualPurchase> findAllByUserId(String userId);

    Optional<UsualPurchase> findByUserIdAndProductId(String userId, String productId);
}
