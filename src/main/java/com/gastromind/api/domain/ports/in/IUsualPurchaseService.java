package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.UsualPurchase;

import java.util.List;

public interface IUsualPurchaseService {
    List<UsualPurchase> findAll();
    UsualPurchase findById(String id);
    UsualPurchase create(UsualPurchase usualPurchase);
    UsualPurchase update(String id, UsualPurchase usualPurchase);
    void delete(String id);
}