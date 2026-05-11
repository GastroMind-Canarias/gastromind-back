package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Allergen;

import java.util.List;

/**
 * Puerto de entrada para administrar el catálogo de alérgenos (listado, alta, edición y borrado).
 */
public interface IAllergenService {
    List<Allergen> findAll();
    Allergen findById(String id);
    Allergen create(Allergen allergen);
    Allergen update(String id, Allergen allergen);
    void delete(String id);
}
