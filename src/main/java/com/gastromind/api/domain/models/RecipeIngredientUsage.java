package com.gastromind.api.domain.models;

import java.math.BigDecimal;

/**
 * Modelo de dominio para el uso de un ingrediente en receta.
 */
public class RecipeIngredientUsage {

    private String productId;
    private String productName;
    private BigDecimal quantityUsed;
    private BigDecimal quantityAvailable;
    /**
     * Crea una nueva instancia.
     */

    public RecipeIngredientUsage() {
    }
    /**
     * Crea una nueva instancia.
     * @param productId valor a utilizar.
     * @param productName valor a utilizar.
     * @param quantityUsed valor a utilizar.
     * @param quantityAvailable valor a utilizar.
     */

    public RecipeIngredientUsage(String productId, String productName, BigDecimal quantityUsed,
            BigDecimal quantityAvailable) {
        this.productId = productId;
        this.productName = productName;
        this.quantityUsed = quantityUsed;
        this.quantityAvailable = quantityAvailable;
    }
    /**
     * Devuelve product id.
     * @return el valor actual
     */

    public String getProductId() {
        return productId;
    }
    /**
     * Define product id.
     * @param productId valor a utilizar.
     */

    public void setProductId(String productId) {
        this.productId = productId;
    }
    /**
     * Devuelve product name.
     * @return el valor actual
     */

    public String getProductName() {
        return productName;
    }
    /**
     * Define product name.
     * @param productName valor a utilizar.
     */

    public void setProductName(String productName) {
        this.productName = productName;
    }
    /**
     * Devuelve quantity used.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getQuantityUsed() {
        return quantityUsed;
    }
    /**
     * Define quantity used.
     * @param quantityUsed valor a utilizar.
     */

    public void setQuantityUsed(BigDecimal quantityUsed) {
        this.quantityUsed = quantityUsed;
    }
    /**
     * Devuelve quantity available.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getQuantityAvailable() {
        return quantityAvailable;
    }
    /**
     * Define quantity available.
     * @param quantityAvailable valor a utilizar.
     */

    public void setQuantityAvailable(BigDecimal quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
}
