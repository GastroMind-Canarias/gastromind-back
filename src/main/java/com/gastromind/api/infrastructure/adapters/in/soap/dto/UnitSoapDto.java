package com.gastromind.api.infrastructure.adapters.in.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Unidad de medida tal como la vería un cliente SOAP externo (id + nombre legible).
 */
@XmlRootElement(name = "unit")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnitSoap", propOrder = {"id", "name"})
public class UnitSoapDto {

    private String id;
    private String name;

    public UnitSoapDto() {
    }

    public UnitSoapDto(String id, String name) {
        this.id = id;
        this.name = name;
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
}
