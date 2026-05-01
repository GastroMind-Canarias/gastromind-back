package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product")
/**
 * Entidad JPA para persistir productos del catalogo.
 */
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "is_essential")
    private Boolean isEssential;

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
    /** Constructor vacio requerido por JPA. */

    public ProductEntity() {
    }
    /** Constructor auxiliar cuando solo se conoce el identificador. */

    public ProductEntity(String id) {
        this.id = id;
    }
    /** Constructor completo de la entidad de producto. */

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
     * Devuelve name.
     * @return valor actual.
     */

    public String getName() {
        return name;
    }
    /**
     * Define name.
     * @param name el nombre
     */

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Devuelve is essential.
     * @return true si cumple la condicion; false en caso contrario.
     */

    public Boolean getIsEssential() {
        return isEssential;
    }
    /**
     * Define is essential.
     * @param isEssential valor a utilizar.
     */

    public void setIsEssential(Boolean isEssential) {
        this.isEssential = isEssential;
    }
    /**
     * Devuelve needs review.
     * @return true si cumple la condicion; false en caso contrario.
     */

    public Boolean getNeedsReview() {
        return needsReview;
    }
    /**
     * Define needs review.
     * @param needsReview valor a utilizar.
     */

    public void setNeedsReview(Boolean needsReview) {
        this.needsReview = needsReview;
    }
    /**
     * Devuelve review note.
     * @return valor actual.
     */

    public String getReviewNote() {
        return reviewNote;
    }
    /**
     * Define review note.
     * @param reviewNote valor a utilizar.
     */

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }
    /**
     * Devuelve category.
     * @return resultado de la operacion solicitada.
     */

    public CategoryEntity getCategory() {
        return category;
    }
    /**
     * Define category.
     * @param category la categoria
     */

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
    /**
     * Devuelve allergens.
     * @return resultado de la operacion solicitada.
     */

    public Set<AllergenEntity> getAllergens() {
        return allergens;
    }
    /**
     * Define allergens.
     * @param allergens valor a utilizar.
     */

    public void setAllergens(Set<AllergenEntity> allergens) {
        this.allergens = allergens;
    }
    /**
     * Devuelve ticket items.
     * @return lista actual.
     */

    public List<TicketItemEntity> getTicketItems() {
        return ticketItems;
    }
    /**
     * Define ticket items.
     * @param ticketItems valor a utilizar.
     */

    public void setTicketItems(List<TicketItemEntity> ticketItems) {
        this.ticketItems = ticketItems;
    }
    /**
     * Devuelve fridge items.
     * @return lista actual.
     */

    public List<FridgeItemEntity> getFridgeItems() {
        return fridgeItems;
    }
    /**
     * Define fridge items.
     * @param fridgeItems valor a utilizar.
     */

    public void setFridgeItems(List<FridgeItemEntity> fridgeItems) {
        this.fridgeItems = fridgeItems;
    }
    /**
     * Devuelve recipe ingredients.
     * @return lista actual.
     */

    public List<RecipeIngredientEntity> getRecipeIngredients() {
        return recipeIngredients;
    }
    /**
     * Define recipe ingredients.
     * @param recipeIngredients valor a utilizar.
     */

    public void setRecipeIngredients(List<RecipeIngredientEntity> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
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
        ProductEntity other = (ProductEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




