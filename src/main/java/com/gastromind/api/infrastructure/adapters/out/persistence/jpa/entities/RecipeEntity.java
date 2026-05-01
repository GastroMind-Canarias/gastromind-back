package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ApplianceType;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.DifficultyLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipe")
/**
 * Representa recipe dentro del dominio de la aplicacion.
 */
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private Integer servings;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "appliance_needed")
    private ApplianceType applianceNeeded;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;

    @OneToMany(mappedBy = "recipe")
    private List<RecipeIngredientEntity> ingredients;

    @ManyToMany(mappedBy = "favoriteRecipes")
    private List<UserEntity> favoritedBy;
    /**
     * Constructor de recipe.
     */

    public RecipeEntity() {
    }
    /**
     * Constructor de recipe.
     * @param id el identificador del recurso
     */

    public RecipeEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de recipe.
     * @param id el identificador del recurso
     * @param title valor a utilizar.
     * @param instructions valor a utilizar.
     * @param servings valor a utilizar.
     * @param prepTimeMinutes valor a utilizar.
     * @param createdAt valor a utilizar.
     * @param applianceNeeded valor a utilizar.
     * @param difficulty valor a utilizar.
     * @param ingredients valor a utilizar.
     * @param favoritedBy valor a utilizar.
     */

    public RecipeEntity(String id, String title, String instructions, Integer servings, Integer prepTimeMinutes,
            LocalDateTime createdAt, ApplianceType applianceNeeded, DifficultyLevel difficulty,
            List<RecipeIngredientEntity> ingredients, List<UserEntity> favoritedBy) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.servings = servings;
        this.prepTimeMinutes = prepTimeMinutes;
        this.createdAt = createdAt;
        this.applianceNeeded = applianceNeeded;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.favoritedBy = favoritedBy;
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
     * Devuelve title.
     * @return valor actual.
     */

    public String getTitle() {
        return title;
    }
    /**
     * Define title.
     * @param title valor a utilizar.
     */

    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Devuelve instructions.
     * @return valor actual.
     */

    public String getInstructions() {
        return instructions;
    }
    /**
     * Define instructions.
     * @param instructions valor a utilizar.
     */

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    /**
     * Devuelve servings.
     * @return valor configurado.
     */

    public Integer getServings() {
        return servings;
    }
    /**
     * Define servings.
     * @param servings valor a utilizar.
     */

    public void setServings(Integer servings) {
        this.servings = servings;
    }
    /**
     * Devuelve prep time minutes.
     * @return valor configurado.
     */

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }
    /**
     * Define prep time minutes.
     * @param prepTimeMinutes valor a utilizar.
     */

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
    /**
     * Devuelve created at.
     * @return resultado de la operacion solicitada.
     */

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /**
     * Define created at.
     * @param createdAt valor a utilizar.
     */

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * Devuelve appliance needed.
     * @return resultado de la operacion solicitada.
     */

    public ApplianceType getApplianceNeeded() {
        return applianceNeeded;
    }
    /**
     * Define appliance needed.
     * @param applianceNeeded valor a utilizar.
     */

    public void setApplianceNeeded(ApplianceType applianceNeeded) {
        this.applianceNeeded = applianceNeeded;
    }
    /**
     * Devuelve difficulty.
     * @return resultado de la operacion solicitada.
     */

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }
    /**
     * Define difficulty.
     * @param difficulty valor a utilizar.
     */

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * Devuelve ingredients.
     * @return lista actual.
     */

    public List<RecipeIngredientEntity> getIngredients() {
        return ingredients;
    }
    /**
     * Define ingredients.
     * @param ingredients valor a utilizar.
     */

    public void setIngredients(List<RecipeIngredientEntity> ingredients) {
        this.ingredients = ingredients;
    }
    /**
     * Devuelve favorited by.
     * @return lista actual.
     */

    public List<UserEntity> getFavoritedBy() {
        return favoritedBy;
    }
    /**
     * Define favorited by.
     * @param favoritedBy valor a utilizar.
     */

    public void setFavoritedBy(List<UserEntity> favoritedBy) {
        this.favoritedBy = favoritedBy;
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
        RecipeEntity other = (RecipeEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




