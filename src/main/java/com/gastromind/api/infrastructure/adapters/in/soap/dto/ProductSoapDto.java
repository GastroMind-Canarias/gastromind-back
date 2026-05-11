package com.gastromind.api.infrastructure.adapters.in.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Vista aplanada de producto: referencias a alérgeno y categoría van como ids y nombres
 * para no anidar grafos JAXB innecesarios en la práctica universitaria.
 */
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ProductSoap",
        propOrder = {
                "id", "name", "essential", "needsReview", "reviewNote",
                "allergenId", "allergenName", "categoryId", "categoryName"
        })
public class ProductSoapDto {

    private String id;
    private String name;
    private boolean essential;
    private boolean needsReview;
    private String reviewNote;
    private String allergenId;
    private String allergenName;
    private String categoryId;
    private String categoryName;

    public ProductSoapDto() {
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

    public boolean isEssential() {
        return essential;
    }

    public void setEssential(boolean essential) {
        this.essential = essential;
    }

    public boolean isNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public String getAllergenId() {
        return allergenId;
    }

    public void setAllergenId(String allergenId) {
        this.allergenId = allergenId;
    }

    public String getAllergenName() {
        return allergenName;
    }

    public void setAllergenName(String allergenName) {
        this.allergenName = allergenName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
