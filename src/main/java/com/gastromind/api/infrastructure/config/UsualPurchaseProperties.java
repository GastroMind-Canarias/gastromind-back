package com.gastromind.api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.usual-purchase")
public class UsualPurchaseProperties {

    /** Días hacia atrás para considerar tickets del hogar (todos los miembros). */
    private int historyDays = 90;

    /** Mínimo de tickets distintos con el producto para considerarlo "habitual". */
    private int minDistinctTickets = 2;

    /** Stock en nevera por debajo de target × esta fracción ⇒ bajo. */
    private double lowStockFraction = 0.3;

    /** Half-life en días para decaimiento por antigüedad de la última compra (score). */
    private double recencyHalfLifeDays = 30.0;

    /** Peso de la nueva observación al fusionar tras guardar un ticket (0–1). */
    private double ticketSyncBlendWeight = 0.35;

    public int getHistoryDays() {
        return historyDays;
    }

    public void setHistoryDays(int historyDays) {
        this.historyDays = historyDays;
    }

    public int getMinDistinctTickets() {
        return minDistinctTickets;
    }

    public void setMinDistinctTickets(int minDistinctTickets) {
        this.minDistinctTickets = minDistinctTickets;
    }

    public double getLowStockFraction() {
        return lowStockFraction;
    }

    public void setLowStockFraction(double lowStockFraction) {
        this.lowStockFraction = lowStockFraction;
    }

    public double getRecencyHalfLifeDays() {
        return recencyHalfLifeDays;
    }

    public void setRecencyHalfLifeDays(double recencyHalfLifeDays) {
        this.recencyHalfLifeDays = recencyHalfLifeDays;
    }

    public double getTicketSyncBlendWeight() {
        return ticketSyncBlendWeight;
    }

    public void setTicketSyncBlendWeight(double ticketSyncBlendWeight) {
        this.ticketSyncBlendWeight = ticketSyncBlendWeight;
    }
}
