package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Modelo de dominio para una línea de ticket.
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
    /**
     * Crea una nueva instancia.
     */

    public TicketItem() {
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param product el producto
     * @param quantity la cantidad
     * @param priceUnit valor a utilizar.
     */

    public TicketItem(String id, Product product, BigDecimal quantity, BigDecimal priceUnit) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.priceUnit = priceUnit;
    }
    /**
     * Devuelve id.
     * @return el valor actual
     */

    public String getId() {
        return id;
    }
    /**
     * Define id.
     * @param id el identificador del recurso
     */

    public void setId(String id) {
        this.id = id;
    }
    /**
     * Devuelve product.
     * @return resultado de la operacion solicitada.
     */

    public Product getProduct() {
        return product;
    }
    /**
     * Define product.
     * @param product el producto
     */

    public void setProduct(Product product) {
        this.product = product;
    }
    /**
     * Devuelve quantity.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getQuantity() {
        return quantity;
    }
    /**
     * Define quantity.
     * @param quantity la cantidad
     */

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    /**
     * Devuelve unit.
     * @return resultado de la operacion solicitada.
     */

    public Unit getUnit() {
        return unit;
    }
    /**
     * Define unit.
     * @param unit la unidad
     */

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    /**
     * Devuelve price unit.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }
    /**
     * Define price unit.
     * @param priceUnit valor a utilizar.
     */

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }
    /**
     * Devuelve verification status.
     * @return resultado de la operacion solicitada.
     */

    public TicketLineVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }
    /**
     * Define verification status.
     * @param verificationStatus valor a utilizar.
     */

    public void setVerificationStatus(TicketLineVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    /**
     * Devuelve line note.
     * @return el valor actual
     */

    public String getLineNote() {
        return lineNote;
    }
    /**
     * Define line note.
     * @param lineNote valor a utilizar.
     */

    public void setLineNote(String lineNote) {
        this.lineNote = lineNote;
    }
    /**
     * Devuelve line product name.
     * @return el valor actual
     */

    public String getLineProductName() {
        return lineProductName;
    }
    /**
     * Define line product name.
     * @param lineProductName valor a utilizar.
     */

    public void setLineProductName(String lineProductName) {
        this.lineProductName = lineProductName;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicketItem that = (TicketItem) o;
        return Objects.equals(id, that.id);
    }
    /**
     * Calcula el hash de esta instancia.
     * @return el hash calculado
     */

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
