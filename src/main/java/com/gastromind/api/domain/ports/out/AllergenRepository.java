package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Allergen;

public interface AllergenRepository {
    Allergen save(Allergen allergen);

    Optional<Allergen> findById(String id);

    void deleteById(String id);

    List<Allergen> findAll();
}
