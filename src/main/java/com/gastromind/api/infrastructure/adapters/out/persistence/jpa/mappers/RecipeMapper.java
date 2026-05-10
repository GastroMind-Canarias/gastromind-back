package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeIngredientEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.DifficultyLevel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de recipe.
 */
public interface RecipeMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = "ingredientsUsed")
    @Mapping(source = "prep_time", target = "prepTimeMinutes")
    @Mapping(source = "appliance_needed", target = "applianceNeeded")
    @Mapping(source = "created_at", target = "createdAt")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "mapDifficultyLevel")
    RecipeEntity toEntity(Recipe domain);

    @Mapping(source = "prepTimeMinutes", target = "prep_time")
    @Mapping(source = "applianceNeeded", target = "appliance_needed")
    @Mapping(source = "createdAt", target = "created_at")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "difficultyLevelToString")
    @Mapping(target = "ingredientsUsed", ignore = true)
    Recipe toDomain(RecipeEntity entity);

    List<RecipeEntity> toEntityList(List<Recipe> domainList);
    List<Recipe> toDomainList(List<RecipeEntity> entityList);

    @Named("mapDifficultyLevel")
    default DifficultyLevel mapDifficultyLevel(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            return DifficultyLevel.MEDIUM;
        }
        String s = difficulty.trim();
        for (DifficultyLevel level : DifficultyLevel.values()) {
            if (level.name().equalsIgnoreCase(s)) {
                return level;
            }
        }
        switch (s.toLowerCase(Locale.ROOT)) {
            case "facil":
            case "easy":
                return DifficultyLevel.EASY;
            case "media":
            case "medio":
            case "medium":
                return DifficultyLevel.MEDIUM;
            case "alta":
            case "dificil":
            case "hard":
                return DifficultyLevel.HARD;
            default:
                return DifficultyLevel.MEDIUM;
        }
    }

    @Named("difficultyLevelToString")
    default String difficultyLevelToString(DifficultyLevel level) {
        return level == null ? null : level.name();
    }

    /**
     * Igual que {@link #toDomain(RecipeEntity)} pero rellena ingredientes desde filas JPA.
     * Solo conviene cuando la entidad ya trae {@code ingredients} cargados (p. ej. favoritos con fetch),
     * para no disparar consultas extra en listados genericos de recetas.
     */
    @Named("toDomainWithIngredients")
    default Recipe toDomainWithIngredients(RecipeEntity entity) {
        if (entity == null) {
            return null;
        }
        Recipe recipe = toDomain(entity);
        List<RecipeIngredientEntity> rows = entity.getIngredients();
        if (rows == null || rows.isEmpty()) {
            return recipe;
        }
        List<RecipeIngredientUsage> usages = new ArrayList<>();
        for (RecipeIngredientEntity row : rows) {
            if (row == null) {
                continue;
            }
            RecipeIngredientUsage u = new RecipeIngredientUsage();
            if (row.getProduct() != null) {
                u.setProductId(row.getProduct().getId());
                u.setProductName(row.getProduct().getName());
            }
            u.setQuantityUsed(row.getQuantityRequired());
            u.setQuantityAvailable(null);
            usages.add(u);
        }
        recipe.setIngredientsUsed(usages);
        return recipe;
    }
}






