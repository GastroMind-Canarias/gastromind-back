package com.gastromind.api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.usual-purchase")
/**
 * Ajustes de negocio para calcular sugerencias de compra habitual.
 */
public class UsualPurchaseProperties {

    private int historyDays = 90;

    private int minDistinctTickets = 2;

    private double lowStockFraction = 0.3;

    private double recencyHalfLifeDays = 30.0;

    private double ticketSyncBlendWeight = 0.35;
    /** Días de historial que se tienen en cuenta para el cálculo. */

    public int getHistoryDays() {
        return historyDays;
    }
    /** Define la ventana de historial analizada. */

    public void setHistoryDays(int historyDays) {
        this.historyDays = historyDays;
    }
    /** Número mínimo de tickets distintos para generar sugerencias fiables. */

    public int getMinDistinctTickets() {
        return minDistinctTickets;
    }
    /** Define el umbral mínimo de tickets distintos. */

    public void setMinDistinctTickets(int minDistinctTickets) {
        this.minDistinctTickets = minDistinctTickets;
    }
    /** Proporción que marca cuándo un producto se considera con poco stock. */

    public double getLowStockFraction() {
        return lowStockFraction;
    }
    /** Define el umbral de bajo stock. */

    public void setLowStockFraction(double lowStockFraction) {
        this.lowStockFraction = lowStockFraction;
    }
    /** Semivida en días para ponderar la recencia de compras anteriores. */

    public double getRecencyHalfLifeDays() {
        return recencyHalfLifeDays;
    }
    /** Define la semivida aplicada a la recencia. */

    public void setRecencyHalfLifeDays(double recencyHalfLifeDays) {
        this.recencyHalfLifeDays = recencyHalfLifeDays;
    }
    /** Peso de mezcla aplicado al sincronizar sugerencias con tickets. */

    public double getTicketSyncBlendWeight() {
        return ticketSyncBlendWeight;
    }
    /** Define el peso de sincronización con tickets. */

    public void setTicketSyncBlendWeight(double ticketSyncBlendWeight) {
        this.ticketSyncBlendWeight = ticketSyncBlendWeight;
    }
}




