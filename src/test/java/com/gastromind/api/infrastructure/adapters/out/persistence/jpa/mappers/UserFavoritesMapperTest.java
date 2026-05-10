package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeIngredientEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Comprueba que el mapeo entidad→dominio usa la receta hidratada (ingredientes), no solo el id.
 */
class UserFavoritesMapperTest {

    @Test
    void toDomain_delegatesRecipeTo_toDomainWithIngredients() {
        UserFavoritesMapperImpl mapper = new UserFavoritesMapperImpl();
        ReflectionTestUtils.setField(mapper, "recipeMapper", new RecipeMapperImpl());
        ReflectionTestUtils.setField(mapper, "userMapper", new UserMapperImpl());

        ProductEntity product = new ProductEntity();
        product.setId("p-1");
        product.setName("Leche");

        RecipeIngredientEntity line = new RecipeIngredientEntity();
        line.setProduct(product);
        line.setQuantityRequired(BigDecimal.ONE);

        RecipeEntity recipe = new RecipeEntity();
        recipe.setId("r-1");
        recipe.setTitle("Flan");
        recipe.setIngredients(List.of(line));

        UserEntity user = new UserEntity();
        user.setId("u-1");

        UserFavoritesEntity entity = new UserFavoritesEntity();
        entity.setId("fav-1");
        entity.setUser(user);
        entity.setRecipe(recipe);

        UserFavorites out = mapper.toDomain(entity);

        assertEquals("fav-1", out.getId());
        assertEquals("u-1", out.getUser_id().getId());
        assertEquals(1, out.getRecipe_id().getIngredientsUsed().size());
        assertEquals("p-1", out.getRecipe_id().getIngredientsUsed().get(0).getProductId());
        assertEquals("Leche", out.getRecipe_id().getIngredientsUsed().get(0).getProductName());
    }
}
