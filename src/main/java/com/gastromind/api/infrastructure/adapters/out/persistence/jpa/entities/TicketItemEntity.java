package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_items")
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

    public TicketItemEntity() {
    }

    public TicketItemEntity(String id) {
        this.id = id;
    }

    public TicketItemEntity(String id, BigDecimal quantity, BigDecimal priceUnit, TicketEntity ticket,
            ProductEntity product) {
        this.id = id;
        this.quantity = quantity;
        this.priceUnit = priceUnit;
        this.ticket = ticket;
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UnitEntity getUnit() {
        return unit;
    }

    public void setUnit(UnitEntity unit) {
        this.unit = unit;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

    public TicketEntity getTicket() {
        return ticket;
    }

    public void setTicket(TicketEntity ticket) {
        this.ticket = ticket;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public String getLineProductName() {
        return lineProductName;
    }

    public void setLineProductName(String lineProductName) {
        this.lineProductName = lineProductName;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

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
