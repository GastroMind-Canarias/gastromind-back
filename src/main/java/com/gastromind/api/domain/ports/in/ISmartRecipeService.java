package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Recipe;

public interface ISmartRecipeService {
    /**
     * Obtiene una sugerencia de receta basada en el inventario actual de una
     * nevera.
     * Retorna un objeto Recipe con todos los campos populados por la IA.
     */
    Recipe suggestRecipeForFridge(String fridgeId);
}
