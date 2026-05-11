package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserFavoritesRestMapperTest {

    @Test
    void toResponse_nestsFullRecipe() {
        UserFavoritesRestMapperImpl mapper = new UserFavoritesRestMapperImpl();
        ReflectionTestUtils.setField(mapper, "recipeRestMapper", new RecipeRestMapperImpl());

        RecipeIngredientUsage ing = new RecipeIngredientUsage(
                "prod-1", "Arroz", new BigDecimal("200"), null);
        Recipe recipe = new Recipe(
                "rec-7", "Paella", "Sofreir...", 4, 90, Appliance.VITROCERAMICA,
                "MEDIUM", LocalDate.of(2026, 1, 15));
        recipe.setIngredientsUsed(List.of(ing));

        UserFavorites fav = new UserFavorites("fav-99", new User("usr-3"), recipe);

        UserFavoritesResponse out = mapper.toResponse(fav);

        assertEquals("fav-99", out.id());
        assertEquals("usr-3", out.user_id());
        RecipeResponse nested = out.recipe();
        assertNotNull(nested);
        assertEquals("rec-7", nested.id());
        assertEquals("Paella", nested.title());
        assertEquals("Sofreir...", nested.instructions());
        assertEquals(1, nested.ingredientsUsed().size());
        assertEquals("prod-1", nested.ingredientsUsed().get(0).productId());
    }
}
