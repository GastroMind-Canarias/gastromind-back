package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.models.ConsumeRecipeOutcome;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.FridgeItemConsumeLine;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsumeMyFridgeItemsFromRecipeUseCaseTest {

    @Test
    void execute_shouldConsumeExpiredFirstThenByExpirationDate() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem fresh = item("item-fresh", "product-1", new BigDecimal("2.00"),
                LocalDate.of(2026, 6, 10), ItemStatus.IN_FRIDGE);
        FridgeItem soon = item("item-soon", "product-1", new BigDecimal("1.00"),
                LocalDate.of(2026, 5, 20), ItemStatus.GOOD);
        FridgeItem expired = item("item-expired", "product-1", new BigDecimal("0.50"),
                LocalDate.of(2026, 4, 1), ItemStatus.EXPIRED);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(fresh, soon, expired));
        when(fridgeItemService.consumePartiallyBatch(any())).thenReturn(List.of(expired, soon));

        List<RecipeIngredientUsage> ingredients = List.of(
                new RecipeIngredientUsage("product-1", "Tomate", new BigDecimal("1.20"), null));

        ConsumeRecipeOutcome outcome = useCase.execute("member1", ingredients);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FridgeItemConsumeLine>> captor = ArgumentCaptor.forClass(List.class);
        verify(fridgeItemService).consumePartiallyBatch(captor.capture());
        List<FridgeItemConsumeLine> lines = captor.getValue();

        assertEquals(2, lines.size());
        assertEquals("item-expired", lines.get(0).itemId());
        assertEquals(0, lines.get(0).quantity().compareTo(new BigDecimal("0.50")));
        assertEquals("item-soon", lines.get(1).itemId());
        assertEquals(0, lines.get(1).quantity().compareTo(new BigDecimal("0.70")));
        assertTrue(outcome.ignored().isEmpty());
        assertEquals(2, outcome.consumed().size());
    }

    @Test
    void execute_shouldFollowFifoByExpirationWhenNoneExpired() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem later = item("item-later", "product-1", new BigDecimal("3.00"),
                LocalDate.of(2026, 7, 1), ItemStatus.GOOD);
        FridgeItem earlier = item("item-earlier", "product-1", new BigDecimal("2.00"),
                LocalDate.of(2026, 5, 1), ItemStatus.GOOD);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(later, earlier));
        when(fridgeItemService.consumePartiallyBatch(any())).thenReturn(List.of(earlier));

        useCase.execute("member1", List.of(new RecipeIngredientUsage(
                "product-1", "Tomate", new BigDecimal("1.50"), null)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FridgeItemConsumeLine>> captor = ArgumentCaptor.forClass(List.class);
        verify(fridgeItemService).consumePartiallyBatch(captor.capture());

        assertEquals(1, captor.getValue().size());
        assertEquals("item-earlier", captor.getValue().get(0).itemId());
        assertEquals(0, captor.getValue().get(0).quantity().compareTo(new BigDecimal("1.50")));
    }

    @Test
    void execute_shouldPushItemsWithoutExpirationDateToTheEnd() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem noDate = item("item-no-date", "product-1", new BigDecimal("1.00"),
                null, ItemStatus.GOOD);
        FridgeItem dated = item("item-dated", "product-1", new BigDecimal("0.50"),
                LocalDate.of(2026, 5, 1), ItemStatus.GOOD);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(noDate, dated));
        when(fridgeItemService.consumePartiallyBatch(any())).thenReturn(List.of(dated, noDate));

        useCase.execute("member1", List.of(new RecipeIngredientUsage(
                "product-1", "Tomate", new BigDecimal("0.80"), null)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FridgeItemConsumeLine>> captor = ArgumentCaptor.forClass(List.class);
        verify(fridgeItemService).consumePartiallyBatch(captor.capture());

        assertEquals("item-dated", captor.getValue().get(0).itemId());
        assertEquals("item-no-date", captor.getValue().get(1).itemId());
        assertEquals(0, captor.getValue().get(1).quantity().compareTo(new BigDecimal("0.30")));
    }

    @Test
    void execute_shouldIgnoreIngredientsWithoutProductId() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem tomato = item("item-1", "product-1", new BigDecimal("2.00"),
                LocalDate.of(2026, 6, 1), ItemStatus.GOOD);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(tomato));
        when(fridgeItemService.consumePartiallyBatch(any())).thenReturn(List.of(tomato));

        ConsumeRecipeOutcome outcome = useCase.execute("member1", List.of(
                new RecipeIngredientUsage("product-1", "Tomate", new BigDecimal("0.50"), null),
                new RecipeIngredientUsage(null, "Sal", new BigDecimal("0.10"), null),
                new RecipeIngredientUsage("  ", "Pimienta", new BigDecimal("0.05"), null)));

        assertEquals(2, outcome.ignored().size());
        assertEquals("Sal", outcome.ignored().get(0).productName());
        assertEquals("Sin productId", outcome.ignored().get(0).reason());
        assertNull(outcome.ignored().get(0).productId());
        assertEquals("Pimienta", outcome.ignored().get(1).productName());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FridgeItemConsumeLine>> captor = ArgumentCaptor.forClass(List.class);
        verify(fridgeItemService).consumePartiallyBatch(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("item-1", captor.getValue().get(0).itemId());
    }

    @Test
    void execute_shouldFailWhenStockInsufficientWithoutConsumingAnything() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem little = item("item-1", "product-1", new BigDecimal("0.20"),
                LocalDate.of(2026, 6, 1), ItemStatus.GOOD);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(little));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                "member1", List.of(new RecipeIngredientUsage(
                        "product-1", "Tomate", new BigDecimal("1.00"), null))));

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        assertTrue(ex.getMessage().contains("Tomate"));
        assertTrue(ex.getMessage().contains("0.80"));
        verify(fridgeItemService, never()).consumePartiallyBatch(any());
    }

    @Test
    void execute_shouldFailWhenProductHasNoItemsInFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                "member1", List.of(new RecipeIngredientUsage(
                        "product-99", "Atun", new BigDecimal("0.30"), null))));

        assertTrue(ex.getMessage().contains("Atun"));
        verify(fridgeItemService, never()).consumePartiallyBatch(any());
    }

    @Test
    void execute_shouldUseFallbackLabelWhenProductNameMissing() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                "member1", List.of(new RecipeIngredientUsage(
                        "product-X", null, new BigDecimal("0.30"), null))));

        assertTrue(ex.getMessage().contains("product-X"));
    }

    @Test
    void execute_shouldReturnEmptyConsumedWhenOnlyIgnoredIngredients() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of());

        ConsumeRecipeOutcome outcome = useCase.execute("member1", List.of(
                new RecipeIngredientUsage(null, "Sal", new BigDecimal("0.05"), null)));

        assertTrue(outcome.consumed().isEmpty());
        assertEquals(1, outcome.ignored().size());
        verify(fridgeItemService, never()).consumePartiallyBatch(any());
    }

    @Test
    void execute_shouldSkipNullIngredientEntries() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        FridgeItemServiceImpl fridgeItemService = mock(FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsFromRecipeUseCase useCase = new ConsumeMyFridgeItemsFromRecipeUseCase(
                resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem tomato = item("item-1", "product-1", new BigDecimal("2.00"),
                LocalDate.of(2026, 6, 1), ItemStatus.GOOD);

        when(resolveUseCase.execute("member1")).thenReturn(context("fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(tomato));
        when(fridgeItemService.consumePartiallyBatch(any())).thenReturn(List.of(tomato));

        List<RecipeIngredientUsage> ingredients = new java.util.ArrayList<>();
        ingredients.add(new RecipeIngredientUsage("product-1", "Tomate", new BigDecimal("0.50"), null));
        ingredients.add(null);

        ConsumeRecipeOutcome outcome = useCase.execute("member1", ingredients);

        assertTrue(outcome.ignored().isEmpty());
        verify(fridgeItemService).consumePartiallyBatch(any());
    }

    private ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context(String fridgeId) {
        HouseHold household = new HouseHold();
        household.setId("house-1");

        User user = new User();
        user.setId("user-1");
        user.setName("member1");
        user.setRole(Role.ROLE_MEMBER);
        user.setHouseHold_id(household);

        Fridge fridge = new Fridge();
        fridge.setId(fridgeId);
        fridge.setHouseHold_id(household);

        return new ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext(
                user, "house-1", fridge);
    }

    private FridgeItem item(String id, String productId, BigDecimal quantity, LocalDate expiration, ItemStatus status) {
        FridgeItem fi = new FridgeItem();
        fi.setId(id);
        fi.setFridgeId("fridge-1");
        fi.setQuantity(quantity);
        fi.setExpirationDate(expiration);
        fi.setStatus(status);
        Product product = new Product();
        product.setId(productId);
        fi.setProduct(product);
        return fi;
    }
}
