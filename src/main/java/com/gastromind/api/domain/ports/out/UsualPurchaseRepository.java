package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.UsualPurchase;

public interface UsualPurchaseRepository {

    UsualPurchase save(UsualPurchase usualPurchase);

    Optional<UsualPurchase> findById(String id);

    void deleteById(String id);

    List<UsualPurchase> findAll();

}
