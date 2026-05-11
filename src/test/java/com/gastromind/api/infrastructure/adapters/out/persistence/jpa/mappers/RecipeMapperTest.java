package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeIngredientEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ApplianceType;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.DifficultyLevel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecipeMapperTest {

    private final RecipeMapperImpl mapper = new RecipeMapperImpl();

    @Test
    void toDomainWithIngredients_mapsRowsAndLeavesAvailabilityNull() {
        ProductEntity product = new ProductEntity();
        product.setId("prod-99");
        product.setName("Harina");

        RecipeIngredientEntity row = new RecipeIngredientEntity();
        row.setProduct(product);
        row.setQuantityRequired(new BigDecimal("0.25"));

        RecipeEntity entity = new RecipeEntity();
        entity.setId("rec-42");
        entity.setTitle("Pan");
        entity.setInstructions("Amasar");
        entity.setServings(2);
        entity.setPrepTimeMinutes(45);
        entity.setCreatedAt(LocalDateTime.of(2026, 3, 1, 10, 0));
        entity.setApplianceNeeded(ApplianceType.HORNO);
        entity.setDifficulty(DifficultyLevel.EASY);
        entity.setIngredients(List.of(row));

        Recipe out = mapper.toDomainWithIngredients(entity);

        assertEquals("rec-42", out.getId());
        assertEquals("Pan", out.getTitle());
        assertEquals(1, out.getIngredientsUsed().size());
        RecipeIngredientUsage u = out.getIngredientsUsed().get(0);
        assertEquals("prod-99", u.getProductId());
        assertEquals("Harina", u.getProductName());
        assertEquals(new BigDecimal("0.25"), u.getQuantityUsed());
        assertNull(u.getQuantityAvailable());
    }

    @Test
    void toDomainWithIngredients_emptyIngredients_keepsListEmpty() {
        RecipeEntity entity = new RecipeEntity();
        entity.setId("rec-x");
        entity.setTitle("Sopa");
        entity.setIngredients(List.of());

        Recipe out = mapper.toDomainWithIngredients(entity);
        assertEquals(0, out.getIngredientsUsed().size());
    }
}
