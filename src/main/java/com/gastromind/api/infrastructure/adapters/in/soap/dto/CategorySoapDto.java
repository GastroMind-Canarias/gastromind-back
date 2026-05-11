package com.gastromind.api.infrastructure.adapters.in.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Copia mínima de una categoría para el cable SOAP; no intenta ser el modelo de dominio.
 */
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategorySoap", propOrder = {"id", "name"})
public class CategorySoapDto {

    private String id;
    private String name;

    public CategorySoapDto() {
    }

    public CategorySoapDto(String id, String name) {
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
