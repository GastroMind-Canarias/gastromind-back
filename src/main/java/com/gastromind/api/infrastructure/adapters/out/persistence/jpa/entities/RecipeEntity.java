package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ApplianceType;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.DifficultyLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe")
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

    public RecipeEntity() {
    }

    public RecipeEntity(String id) {
        this.id = id;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ApplianceType getApplianceNeeded() {
        return applianceNeeded;
    }

    public void setApplianceNeeded(ApplianceType applianceNeeded) {
        this.applianceNeeded = applianceNeeded;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public List<RecipeIngredientEntity> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientEntity> ingredients) {
        this.ingredients = ingredients;
    }

    public List<UserEntity> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(List<UserEntity> favoritedBy) {
        this.favoritedBy = favoritedBy;
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
        RecipeEntity other = (RecipeEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}