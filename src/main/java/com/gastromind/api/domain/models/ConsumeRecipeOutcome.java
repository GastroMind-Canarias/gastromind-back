package com.gastromind.api.domain.models;

import java.util.List;

/**
 * Resultado de aplicar el consumo de una receta sobre el inventario del hogar.
 * Lleva los items finalmente afectados y los ingredientes que se descartaron
 * para que el cliente pueda mostrar un resumen sin tener que reconstruirlo.
 *
 * @param consumed items que han cambiado tras los descuentos
 * @param ignored ingredientes que no se han aplicado y motivo asociado
 */
public record ConsumeRecipeOutcome(List<FridgeItem> consumed, List<IgnoredIngredient> ignored) {

    /**
     * Linea informativa: ingrediente que entro pero no descuenta stock.
     *
     * @param productId id del producto si venia en la peticion
     * @param productName nombre del producto tal como llego
     * @param reason motivo legible por el cliente
     */
    public record IgnoredIngredient(String productId, String productName, String reason) {
    }
}
