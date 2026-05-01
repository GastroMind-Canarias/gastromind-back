package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_items")
/**
 * Representa ticket item dentro del dominio de la aplicacion.
 */
public class TicketItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitEntity unit;

    @Column(name = "price_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceUnit;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "line_product_name", length = 200)
    private String lineProductName;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 32)
    private TicketLineVerificationStatus verificationStatus = TicketLineVerificationStatus.OK;

    @Column(name = "line_note", length = 500)
    private String lineNote;
    /**
     * Constructor de ticket item.
     */

    public TicketItemEntity() {
    }
    /**
     * Constructor de ticket item.
     * @param id el identificador del recurso
     */

    public TicketItemEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de ticket item.
     * @param id el identificador del recurso
     * @param quantity la cantidad
     * @param priceUnit valor a utilizar.
     * @param ticket el ticket
     * @param product el producto
     */

    public TicketItemEntity(String id, BigDecimal quantity, BigDecimal priceUnit, TicketEntity ticket,
            ProductEntity product) {
        this.id = id;
        this.quantity = quantity;
        this.priceUnit = priceUnit;
        this.ticket = ticket;
        this.product = product;
    }
    /**
     * Devuelve id.
     * @return valor actual.
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

    public UnitEntity getUnit() {
        return unit;
    }
    /**
     * Define unit.
     * @param unit la unidad
     */

    public void setUnit(UnitEntity unit) {
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
     * Devuelve ticket.
     * @return resultado de la operacion solicitada.
     */

    public TicketEntity getTicket() {
        return ticket;
    }
    /**
     * Define ticket.
     * @param ticket el ticket
     */

    public void setTicket(TicketEntity ticket) {
        this.ticket = ticket;
    }
    /**
     * Devuelve product.
     * @return resultado de la operacion solicitada.
     */

    public ProductEntity getProduct() {
        return product;
    }
    /**
     * Define product.
     * @param product el producto
     */

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
    /**
     * Devuelve line product name.
     * @return valor actual.
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
     * @return valor actual.
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
     * Calcula el hash de la instancia.
     * @return valor configurado.
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param obj valor a utilizar.
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TicketItemEntity other = (TicketItemEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




