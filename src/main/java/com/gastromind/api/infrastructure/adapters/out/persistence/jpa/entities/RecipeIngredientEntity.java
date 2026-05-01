package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "recipe_ingredients")
/**
 * Representa recipe ingredient dentro del dominio de la aplicacion.
 */
public class RecipeIngredientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal quantityRequired;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private RecipeEntity recipe;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private UnitEntity unit;
    /**
     * Constructor de recipe ingredient.
     */

    public RecipeIngredientEntity() {
    }
    /**
     * Constructor de recipe ingredient.
     * @param id el identificador del recurso
     */

    public RecipeIngredientEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de recipe ingredient.
     * @param id el identificador del recurso
     * @param quantityRequired valor a utilizar.
     * @param recipe la receta
     * @param product el producto
     * @param unit la unidad
     */

    public RecipeIngredientEntity(String id, BigDecimal quantityRequired, RecipeEntity recipe, ProductEntity product,
            UnitEntity unit) {
        this.id = id;
        this.quantityRequired = quantityRequired;
        this.recipe = recipe;
        this.product = product;
        this.unit = unit;
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
     * Devuelve quantity required.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getQuantityRequired() {
        return quantityRequired;
    }
    /**
     * Define quantity required.
     * @param quantityRequired valor a utilizar.
     */

    public void setQuantityRequired(BigDecimal quantityRequired) {
        this.quantityRequired = quantityRequired;
    }
    /**
     * Devuelve recipe.
     * @return resultado de la operacion solicitada.
     */

    public RecipeEntity getRecipe() {
        return recipe;
    }
    /**
     * Define recipe.
     * @param recipe la receta
     */

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
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
        RecipeIngredientEntity other = (RecipeIngredientEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




