package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Allergen;

public interface IAllergenService {
    List<Allergen> findAll();
    Allergen findById(String id);
    Allergen create(Allergen allergen);
    Allergen update(String id, Allergen allergen);
    void delete(String id);
}
