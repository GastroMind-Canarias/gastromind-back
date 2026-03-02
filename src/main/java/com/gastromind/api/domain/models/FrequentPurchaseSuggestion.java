package com.gastromind.api.domain.models;

/**
 * Resultado del análisis IdentificarComprasHabituales.
 * Representa un producto identificado como compra habitual,
 * junto con la frecuencia de aparición en el histórico de tickets
 * y la cantidad media adquirida por compra.
 */
public class FrequentPurchaseSuggestion {

    private Product product;

    /** Número de tickets en los que apareció el producto */
    private long frequency;

    /** Cantidad media comprada por ticket */
    private double avgQuantity;

    /** Indica si el producto ya está registrado en usual_purchase */
    private boolean alreadyRegistered;

    public FrequentPurchaseSuggestion() {
    }

    public FrequentPurchaseSuggestion(Product product, long frequency, double avgQuantity, boolean alreadyRegistered) {
        this.product = product;
        this.frequency = frequency;
        this.avgQuantity = avgQuantity;
        this.alreadyRegistered = alreadyRegistered;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public double getAvgQuantity() {
        return avgQuantity;
    }

    public void setAvgQuantity(double avgQuantity) {
        this.avgQuantity = avgQuantity;
    }

    public boolean isAlreadyRegistered() {
        return alreadyRegistered;
    }

    public void setAlreadyRegistered(boolean alreadyRegistered) {
        this.alreadyRegistered = alreadyRegistered;
    }
}
