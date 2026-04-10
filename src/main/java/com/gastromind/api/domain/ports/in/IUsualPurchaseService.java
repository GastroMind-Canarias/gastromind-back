package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.UsualPurchase;

import java.util.List;

public interface IUsualPurchaseService {
    List<UsualPurchase> findAll();

    List<UsualPurchase> findAllByUserId(String userId);

    UsualPurchase findById(String id);

    UsualPurchase findByIdForUser(String id, String userId);

    UsualPurchase create(UsualPurchase usualPurchase);

    UsualPurchase update(String id, UsualPurchase usualPurchase);

    UsualPurchase updateForUser(String id, UsualPurchase usualPurchase, String userId);

    void delete(String id);

    void deleteForUser(String id, String userId);
}