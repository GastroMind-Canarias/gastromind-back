package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeStockLine;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.RecipeAiPort;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SuggestRecipeFromHouseholdUseCaseTest {

    @Test
    void execute_shouldAggregateStockAndSanitizeAllergensAndDefaultServings() {
        IHouseHoldService householdService = mock(IHouseHoldService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        RecipeAiPort recipeAiPort = mock(RecipeAiPort.class);
        RecipeSuggestionCachePort cache = mock(RecipeSuggestionCachePort.class);
        SuggestRecipeFromHouseholdUseCase useCase = new SuggestRecipeFromHouseholdUseCase(
                householdService, fridgeRepository, fridgeItemService, recipeAiPort, cache);

        User m1 = userWithAllergens("Avena", " ");
        User m2 = userWithAllergens("Avena", "Lactosa");
        when(householdService.listMembers("house-1")).thenReturn(List.of(m1, m2));

        HouseholdAppliance app = new HouseholdAppliance();
        app.setAppliance(Appliance.HORNO);
        when(householdService.listAppliances("house-1")).thenReturn(List.of(app));

        Fridge f1 = new Fridge();
        f1.setId("fridge-1");
        when(fridgeRepository.findByHouseholdId("house-1")).thenReturn(List.of(f1));
        when(fridgeItemService.findByFridgeId("fridge-1")).thenReturn(List.of(
                item("p-1", "Tomate", "2.5", ItemStatus.GOOD),
                item("p-1", "Tomate", "1.0", ItemStatus.GOOD),
                item("p-2", "Leche", "1.0", ItemStatus.EXPIRED),
                itemWithoutValidProduct()));

        Recipe recipe = new Recipe();
        recipe.setId("r-1");
        when(recipeAiPort.generateOneRecipe(any(HouseholdRecipeContext.class))).thenReturn(recipe);
        when(cache.save("house-1", "user-1", recipe)).thenReturn("suggest-1");

        SuggestRecipeFromHouseholdUseCase.SuggestRecipeResult out = useCase.execute("house-1", "user-1", null);

        assertEquals("suggest-1", out.suggestionId());
        assertEquals("r-1", out.recipe().getId());

        ArgumentCaptor<HouseholdRecipeContext> ctxCaptor = ArgumentCaptor.forClass(HouseholdRecipeContext.class);
        verify(recipeAiPort).generateOneRecipe(ctxCaptor.capture());
        HouseholdRecipeContext ctx = ctxCaptor.getValue();
        assertEquals(2, ctx.servings());
        assertEquals(List.of("Avena", "Lactosa"), ctx.allergenNamesToAvoid());
        assertEquals(List.of(Appliance.HORNO), ctx.availableAppliances());
        assertEquals(1, ctx.availableStock().size());
        RecipeStockLine stock = ctx.availableStock().getFirst();
        assertEquals("p-1", stock.productId());
        assertEquals(new BigDecimal("3.5"), stock.quantityAvailable());
    }

    @Test
    void execute_shouldUseExplicitServings_whenPositiveValueProvided() {
        IHouseHoldService householdService = mock(IHouseHoldService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        RecipeAiPort recipeAiPort = mock(RecipeAiPort.class);
        RecipeSuggestionCachePort cache = mock(RecipeSuggestionCachePort.class);
        SuggestRecipeFromHouseholdUseCase useCase = new SuggestRecipeFromHouseholdUseCase(
                householdService, fridgeRepository, fridgeItemService, recipeAiPort, cache);

        when(householdService.listMembers("house-1")).thenReturn(List.of(new User()));
        when(householdService.listAppliances("house-1")).thenReturn(List.of());
        when(fridgeRepository.findByHouseholdId("house-1")).thenReturn(List.of());
        Recipe recipe = new Recipe();
        when(recipeAiPort.generateOneRecipe(any(HouseholdRecipeContext.class))).thenReturn(recipe);
        when(cache.save("house-1", "user-1", recipe)).thenReturn("suggest-2");

        useCase.execute("house-1", "user-1", 5);

        ArgumentCaptor<HouseholdRecipeContext> ctxCaptor = ArgumentCaptor.forClass(HouseholdRecipeContext.class);
        verify(recipeAiPort).generateOneRecipe(ctxCaptor.capture());
        assertEquals(5, ctxCaptor.getValue().servings());
        assertTrue(ctxCaptor.getValue().availableStock().isEmpty());
    }

    private static User userWithAllergens(String... names) {
        User u = new User();
        int idx = 1;
        for (String n : names) {
            Allergen a = new Allergen();
            a.setId("allergen-" + idx++);
            a.setName(n);
            u.addAllergen(a);
        }
        return u;
    }

    private static FridgeItem item(String productId, String productName, String quantity, ItemStatus status) {
        Product p = new Product();
        p.setId(productId);
        p.setName(productName);
        FridgeItem i = new FridgeItem();
        i.setProduct(p);
        i.setQuantity(new BigDecimal(quantity));
        i.setStatus(status);
        return i;
    }

    private static FridgeItem itemWithoutValidProduct() {
        Product p = new Product();
        p.setId(null);
        p.setName(" ");
        FridgeItem i = new FridgeItem();
        i.setProduct(p);
        i.setQuantity(new BigDecimal("2.0"));
        i.setStatus(ItemStatus.GOOD);
        return i;
    }
}
