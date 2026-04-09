package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "is_essential")
    private Boolean isEssential;

    /** Alta desde ticket u otro origen que requiera revisar el producto en catálogo. */
    @Column(name = "needs_review")
    private Boolean needsReview;

    @Column(name = "review_note", length = 500)
    private String reviewNote;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToMany
    @JoinTable(
            name = "product_allergens",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_product_allergens_product_allergen",
                    columnNames = {"product_id", "allergen_id"}))
    private Set<AllergenEntity> allergens;

    @OneToMany(mappedBy = "product")
    private List<TicketItemEntity> ticketItems;

    @OneToMany(mappedBy = "product")
    private List<FridgeItemEntity> fridgeItems;

    @OneToMany(mappedBy = "product")
    private List<RecipeIngredientEntity> recipeIngredients;

    public ProductEntity() {
    }

    public ProductEntity(String id) {
        this.id = id;
    }

    public ProductEntity(String id, String name, Boolean isEssential, CategoryEntity category,
            Set<AllergenEntity> allergens, List<TicketItemEntity> ticketItems, List<FridgeItemEntity> fridgeItems,
            List<RecipeIngredientEntity> recipeIngredients) {
        this.id = id;
        this.name = name;
        this.isEssential = isEssential;
        this.category = category;
        this.allergens = allergens;
        this.ticketItems = ticketItems;
        this.fridgeItems = fridgeItems;
        this.recipeIngredients = recipeIngredients;
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

    public Boolean getIsEssential() {
        return isEssential;
    }

    public void setIsEssential(Boolean isEssential) {
        this.isEssential = isEssential;
    }

    public Boolean getNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(Boolean needsReview) {
        this.needsReview = needsReview;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public Set<AllergenEntity> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<AllergenEntity> allergens) {
        this.allergens = allergens;
    }

    public List<TicketItemEntity> getTicketItems() {
        return ticketItems;
    }

    public void setTicketItems(List<TicketItemEntity> ticketItems) {
        this.ticketItems = ticketItems;
    }

    public List<FridgeItemEntity> getFridgeItems() {
        return fridgeItems;
    }

    public void setFridgeItems(List<FridgeItemEntity> fridgeItems) {
        this.fridgeItems = fridgeItems;
    }

    public List<RecipeIngredientEntity> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredientEntity> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
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
        ProductEntity other = (ProductEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
