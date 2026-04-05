package com.gastromind.api.domain.models;

import java.math.BigDecimal;

/**
 * Producto del catálogo que la receta usará y cantidad (no superior al stock indicado al generar la sugerencia).
 */
public class RecipeIngredientUsage {

    private String productId;
    private String productName;
    private BigDecimal quantityUsed;
    private BigDecimal quantityAvailable;

    public RecipeIngredientUsage() {
    }

    public RecipeIngredientUsage(String productId, String productName, BigDecimal quantityUsed,
            BigDecimal quantityAvailable) {
        this.productId = productId;
        this.productName = productName;
        this.quantityUsed = quantityUsed;
        this.quantityAvailable = quantityAvailable;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(BigDecimal quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public BigDecimal getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(BigDecimal quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
}
