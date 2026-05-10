package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.FridgeItemConsumeLine;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyFridgeItemUseCasesTest {

    @Test
    void listMyFridgeItems_shouldUseAuthenticatedHouseholdFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        ListMyFridgeItemsUseCase useCase = new ListMyFridgeItemsUseCase(resolveUseCase, fridgeItemRepository);
        FridgeItem item = buildItem("item-1", "fridge-1");

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findByFridgeId("fridge-1")).thenReturn(List.of(item));

        List<FridgeItem> result = useCase.execute("member1");

        assertEquals(1, result.size());
        assertEquals("item-1", result.get(0).getId());
        verify(fridgeItemRepository).findByFridgeId("fridge-1");
    }

    @Test
    void createMyFridgeItem_shouldForceAuthenticatedFridgeScope() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        com.gastromind.api.application.services.TicketProductResolutionService ticketProductResolutionService =
                mock(com.gastromind.api.application.services.TicketProductResolutionService.class);
        CreateMyFridgeItemUseCase useCase =
                new CreateMyFridgeItemUseCase(resolveUseCase, fridgeItemService, ticketProductResolutionService);

        FridgeItem created = buildItem("item-1", "fridge-1");
        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemService.addProductToFridge(
                eq("fridge-1"),
                eq("product-1"),
                eq(new BigDecimal("2.50")),
                eq(LocalDate.of(2026, 4, 20)),
                eq(ItemStatus.GOOD)))
                .thenReturn(created);

        FridgeItem result = useCase.execute(
                "member1",
                "product-1",
                null,
                new BigDecimal("2.50"),
                LocalDate.of(2026, 4, 20),
                ItemStatus.GOOD);

        assertEquals("item-1", result.getId());
        verify(fridgeItemService).addProductToFridge(
                "fridge-1",
                "product-1",
                new BigDecimal("2.50"),
                LocalDate.of(2026, 4, 20),
                ItemStatus.GOOD);
    }

    @Test
    void createMyFridgeItem_shouldResolveByProductNameWhenProductIdMissing() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        com.gastromind.api.application.services.TicketProductResolutionService ticketProductResolutionService =
                mock(com.gastromind.api.application.services.TicketProductResolutionService.class);
        CreateMyFridgeItemUseCase useCase =
                new CreateMyFridgeItemUseCase(resolveUseCase, fridgeItemService, ticketProductResolutionService);

        Product product = new Product("product-2", "leche", false, null);
        FridgeItem created = buildItem("item-2", "fridge-1");

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(ticketProductResolutionService.resolveOrCreateProductFromManualEntry("Leche")).thenReturn(product);
        when(fridgeItemService.addProductToFridge(
                eq("fridge-1"),
                eq("product-2"),
                eq(new BigDecimal("1.00")),
                eq(LocalDate.of(2026, 5, 10)),
                eq(ItemStatus.IN_FRIDGE)))
                .thenReturn(created);

        FridgeItem result = useCase.execute(
                "member1",
                null,
                "Leche",
                new BigDecimal("1.00"),
                LocalDate.of(2026, 5, 10),
                ItemStatus.IN_FRIDGE);

        assertEquals("item-2", result.getId());
        verify(ticketProductResolutionService).resolveOrCreateProductFromManualEntry("Leche");
        verify(fridgeItemService).addProductToFridge(
                "fridge-1",
                "product-2",
                new BigDecimal("1.00"),
                LocalDate.of(2026, 5, 10),
                ItemStatus.IN_FRIDGE);
    }

    @Test
    void createMyFridgeItem_shouldPrioritizeProductIdOverProductName() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        com.gastromind.api.application.services.TicketProductResolutionService ticketProductResolutionService =
                mock(com.gastromind.api.application.services.TicketProductResolutionService.class);
        CreateMyFridgeItemUseCase useCase =
                new CreateMyFridgeItemUseCase(resolveUseCase, fridgeItemService, ticketProductResolutionService);

        FridgeItem created = buildItem("item-3", "fridge-1");
        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemService.addProductToFridge(
                eq("fridge-1"),
                eq("product-1"),
                eq(new BigDecimal("3.00")),
                eq(LocalDate.of(2026, 6, 1)),
                eq(ItemStatus.GOOD)))
                .thenReturn(created);

        FridgeItem result = useCase.execute(
                "member1",
                "product-1",
                "Leche",
                new BigDecimal("3.00"),
                LocalDate.of(2026, 6, 1),
                ItemStatus.GOOD);

        assertEquals("item-3", result.getId());
        verify(fridgeItemService).addProductToFridge(
                "fridge-1",
                "product-1",
                new BigDecimal("3.00"),
                LocalDate.of(2026, 6, 1),
                ItemStatus.GOOD);
        verify(ticketProductResolutionService, never()).resolveOrCreateProductFromManualEntry(any());
    }

    @Test
    void createMyFridgeItem_shouldFailWhenProductIdAndProductNameMissing() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        com.gastromind.api.application.services.TicketProductResolutionService ticketProductResolutionService =
                mock(com.gastromind.api.application.services.TicketProductResolutionService.class);
        CreateMyFridgeItemUseCase useCase =
                new CreateMyFridgeItemUseCase(resolveUseCase, fridgeItemService, ticketProductResolutionService);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                "member1",
                " ",
                " ",
                new BigDecimal("1.00"),
                LocalDate.of(2026, 6, 1),
                ItemStatus.GOOD));
        assertEquals("Debes indicar productId o productName", ex.getMessage());
        verify(fridgeItemService, never()).addProductToFridge(any(), any(), any(), any(), any());
    }

    @Test
    void updateMyFridgeItem_shouldRejectItemOutsideAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        UpdateMyFridgeItemUseCase useCase = new UpdateMyFridgeItemUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-2")).thenReturn(java.util.Optional.of(buildItem("item-2", "fridge-2")));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute("member1", "item-2", buildItem("ignored", "fridge-2")));
        assertEquals("El item no pertenece a la nevera del usuario autenticado", ex.getMessage());
        verify(fridgeItemService, never()).update(any(), any());
    }

    @Test
    void updateMyFridgeItem_shouldKeepCatalogLinkWhenRequestHasNoProduct() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService =
                mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        UpdateMyFridgeItemUseCase useCase = new UpdateMyFridgeItemUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem existing = buildItem("item-1", "fridge-1");
        existing.setProductLabel("etiqueta-libre");
        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-1")).thenReturn(java.util.Optional.of(existing));

        FridgeItem patch = new FridgeItem();
        patch.setQuantity(new BigDecimal("0.50"));
        patch.setExpirationDate(LocalDate.of(2026, 7, 1));
        patch.setStatus(ItemStatus.GOOD);

        FridgeItem updated = buildItem("item-1", "fridge-1");
        updated.setProductLabel("etiqueta-libre");
        updated.setQuantity(new BigDecimal("0.50"));
        when(fridgeItemService.update(eq("item-1"), any())).thenReturn(updated);

        FridgeItem result = useCase.execute("member1", "item-1", patch);

        assertEquals(0, result.getQuantity().compareTo(new BigDecimal("0.5")));
        verify(fridgeItemService).update(eq("item-1"), argThat(item ->
                item.getProduct() != null
                        && "product-1".equals(item.getProduct().getId())
                        && "etiqueta-libre".equals(item.getProductLabel())
                        && item.getQuantity().compareTo(new BigDecimal("0.5")) == 0
                        && ItemStatus.GOOD.equals(item.getStatus())));
    }

    @Test
    void deleteMyFridgeItem_shouldDeleteOnlyInsideAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        DeleteMyFridgeItemUseCase useCase = new DeleteMyFridgeItemUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-1")).thenReturn(java.util.Optional.of(buildItem("item-1", "fridge-1")));

        useCase.execute("member1", "item-1");

        verify(fridgeItemService).delete("item-1");
    }

    @Test
    void consumeMyFridgeItemsBatch_shouldApplyBatchWhenAllItemsInFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsBatchUseCase useCase = new ConsumeMyFridgeItemsBatchUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        FridgeItem i1 = buildItem("item-1", "fridge-1");
        FridgeItem i2 = buildItem("item-2", "fridge-1");
        List<FridgeItemConsumeLine> lines = List.of(
                new FridgeItemConsumeLine("item-1", new BigDecimal("1.00")),
                new FridgeItemConsumeLine("item-2", new BigDecimal("0.50")));

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-1")).thenReturn(Optional.of(i1));
        when(fridgeItemRepository.findById("item-2")).thenReturn(Optional.of(i2));
        when(fridgeItemService.consumePartiallyBatch(any())).thenReturn(List.of(i1, i2));

        List<FridgeItem> result = useCase.execute("member1", lines);

        assertEquals(2, result.size());
        verify(fridgeItemService).consumePartiallyBatch(lines);
    }

    @Test
    void consumeMyFridgeItemsBatch_shouldRejectWhenAnyItemOutsideAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemsBatchUseCase useCase = new ConsumeMyFridgeItemsBatchUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        List<FridgeItemConsumeLine> lines = List.of(
                new FridgeItemConsumeLine("item-1", new BigDecimal("1.00")),
                new FridgeItemConsumeLine("item-2", new BigDecimal("1.00")));

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-1")).thenReturn(Optional.of(buildItem("item-1", "fridge-1")));
        when(fridgeItemRepository.findById("item-2")).thenReturn(Optional.of(buildItem("item-2", "fridge-2")));

        assertThrows(ForbiddenException.class, () -> useCase.execute("member1", lines));
        verify(fridgeItemService, never()).consumePartiallyBatch(any());
    }

    @Test
    void consumeMyFridgeItem_shouldRejectItemOutsideAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        ConsumeMyFridgeItemUseCase useCase = new ConsumeMyFridgeItemUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-2")).thenReturn(java.util.Optional.of(buildItem("item-2", "fridge-2")));

        assertThrows(ForbiddenException.class, () -> useCase.execute("member1", "item-2", new BigDecimal("1.00")));
        verify(fridgeItemService, never()).consumePartially(any(), any());
    }

    @Test
    void markMyFridgeItemConsumed_shouldRejectItemOutsideAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeItemRepository fridgeItemRepository = mock(FridgeItemRepository.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        MarkMyFridgeItemConsumedUseCase useCase = new MarkMyFridgeItemConsumedUseCase(resolveUseCase, fridgeItemRepository, fridgeItemService);

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemRepository.findById("item-2")).thenReturn(java.util.Optional.of(buildItem("item-2", "fridge-2")));

        assertThrows(ForbiddenException.class, () -> useCase.execute("member1", "item-2"));
        verify(fridgeItemService, never()).markAsConsumed(any());
    }

    @Test
    void listMyExpiringFridgeItems_shouldUseAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        ListMyExpiringFridgeItemsUseCase useCase = new ListMyExpiringFridgeItemsUseCase(resolveUseCase, fridgeItemService);
        FridgeItem item = buildItem("item-1", "fridge-1");

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemService.getExpiringItems("fridge-1", 5)).thenReturn(List.of(item));

        List<FridgeItem> result = useCase.execute("member1", 5);

        assertEquals(1, result.size());
        verify(fridgeItemService).getExpiringItems("fridge-1", 5);
    }

    @Test
    void listMyFridgeItemsByCategory_shouldUseAuthenticatedFridge() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        com.gastromind.api.application.services.FridgeItemServiceImpl fridgeItemService = mock(com.gastromind.api.application.services.FridgeItemServiceImpl.class);
        ListMyFridgeItemsByCategoryUseCase useCase = new ListMyFridgeItemsByCategoryUseCase(resolveUseCase, fridgeItemService);
        FridgeItem item = buildItem("item-1", "fridge-1");

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));
        when(fridgeItemService.getInventoryByCategory("fridge-1", "cat-1")).thenReturn(List.of(item));

        List<FridgeItem> result = useCase.execute("member1", "cat-1");

        assertEquals(1, result.size());
        verify(fridgeItemService).getInventoryByCategory("fridge-1", "cat-1");
    }

    private ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext buildContext(
            Role role,
            String householdId,
            String fridgeId
    ) {
        HouseHold household = new HouseHold();
        household.setId(householdId);

        User user = new User();
        user.setId("user-1");
        user.setName("user1");
        user.setRole(role);
        user.setHouseHold_id(household);

        Fridge fridge = new Fridge();
        fridge.setId(fridgeId);
        fridge.setHouseHold_id(household);

        return new ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext(
                user,
                householdId,
                fridge
        );
    }

    private FridgeItem buildItem(String itemId, String fridgeId) {
        FridgeItem item = new FridgeItem();
        item.setId(itemId);
        item.setFridgeId(fridgeId);
        item.setQuantity(new BigDecimal("2.00"));
        item.setExpirationDate(LocalDate.of(2026, 5, 1));
        Product product = new Product();
        product.setId("product-1");
        item.setProduct(product);
        return item;
    }
}
