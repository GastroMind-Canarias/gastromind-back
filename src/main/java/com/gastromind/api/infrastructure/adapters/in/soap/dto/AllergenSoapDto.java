package com.gastromind.api.infrastructure.adapters.in.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Ficha mínima de alérgeno para intercambio XML; basta para listados y consultas puntuales.
 */
@XmlRootElement(name = "allergen")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllergenSoap", propOrder = {"id", "name"})
public class AllergenSoapDto {

    private String id;
    private String name;

    public AllergenSoapDto() {
    }

    public AllergenSoapDto(String id, String name) {
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
