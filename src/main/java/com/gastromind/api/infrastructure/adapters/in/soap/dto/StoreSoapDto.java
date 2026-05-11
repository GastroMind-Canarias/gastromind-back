package com.gastromind.api.infrastructure.adapters.in.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Tienda canónica con nombre normalizado expuesto para quien consulte el catálogo vía SOAP.
 */
@XmlRootElement(name = "store")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreSoap", propOrder = {"id", "name", "nameNorm"})
public class StoreSoapDto {

    private String id;
    private String name;
    private String nameNorm;

    public StoreSoapDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameNorm() {
        return nameNorm;
    }

    public void setNameNorm(String nameNorm) {
        this.nameNorm = nameNorm;
    }
}
