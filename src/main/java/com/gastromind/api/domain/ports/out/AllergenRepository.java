package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Allergen;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para allergen.
 */
public interface AllergenRepository {
    Allergen save(Allergen allergen);

    Optional<Allergen> findById(String id);

    void deleteById(String id);

    List<Allergen> findAll();
}
