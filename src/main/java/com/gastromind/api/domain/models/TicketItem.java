package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Línea de ticket: producto resuelto o texto OCR, cantidades, unidad y estado de verificación frente al catálogo.
 */
public class TicketItem {

    private String id;
    private Product product;
    private BigDecimal quantity;
    private Unit unit;
    private BigDecimal priceUnit;
    private TicketLineVerificationStatus verificationStatus;
    private String lineNote;
    private String lineProductName;

    public TicketItem() {
    }

    public TicketItem(String id, Product product, BigDecimal quantity, BigDecimal priceUnit) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.priceUnit = priceUnit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

    public TicketLineVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(TicketLineVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getLineNote() {
        return lineNote;
    }

    public void setLineNote(String lineNote) {
        this.lineNote = lineNote;
    }

    public String getLineProductName() {
        return lineProductName;
    }

    public void setLineProductName(String lineProductName) {
        this.lineProductName = lineProductName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicketItem that = (TicketItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
