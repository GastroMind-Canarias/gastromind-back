package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Allergen;

import java.util.List;
import java.util.Optional;

/**
 * Listado maestro de alérgenos para etiquetar productos y preferencias.
 */
public interface AllergenRepository {
    Allergen save(Allergen allergen);

    Optional<Allergen> findById(String id);

    void deleteById(String id);

    List<Allergen> findAll();
}
