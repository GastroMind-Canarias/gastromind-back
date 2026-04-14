package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FridgeItemServiceImplTest {

    @Mock
    private FridgeItemRepository repository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private FridgeRepository fridgeRepository;

    @InjectMocks
    private FridgeItemServiceImpl service;

    private FridgeItem existing;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("p-1");
        existing = new FridgeItem();
        existing.setId("fi-1");
        existing.setQuantity(BigDecimal.TEN);
        existing.setProduct(product);
        existing.setFridgeId("fr-1");
    }

    @Test
    void findAll_and_findByFridgeId() {
        when(repository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());
        when(repository.findByFridgeId("fr-1")).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findByFridgeId("fr-1"));
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("x"));
    }

    @Test
    void create_saves() {
        FridgeItem in = new FridgeItem();
        when(repository.save(in)).thenReturn(existing);
        assertEquals(existing, service.create(in));
    }

    @Test
    void addProductToFridge_buildsItem() {
        when(fridgeRepository.findById("fr-1")).thenReturn(Optional.of(new Fridge("fr-1")));
        when(productRepository.findById("p-1")).thenReturn(Optional.of(product));
        when(repository.save(any(FridgeItem.class))).thenAnswer(inv -> {
            FridgeItem i = inv.getArgument(0);
            i.setId("new-id");
            return i;
        });

        FridgeItem out = service.addProductToFridge("fr-1", "p-1", BigDecimal.ONE, LocalDate.now(),
                ItemStatus.IN_FRIDGE);

        assertEquals("fr-1", out.getFridgeId());
        assertEquals(product, out.getProduct());
        assertEquals(ItemStatus.IN_FRIDGE, out.getStatus());
    }

    @Test
    void addLabeledItemToFridge_buildsItemWithoutCatalogProduct() {
        when(fridgeRepository.findById("fr-1")).thenReturn(Optional.of(new Fridge("fr-1")));
        when(repository.save(any(FridgeItem.class))).thenAnswer(inv -> {
            FridgeItem i = inv.getArgument(0);
            i.setId("new-id");
            return i;
        });

        FridgeItem out = service.addLabeledItemToFridge("fr-1", "  Yogur sin catálogo  ", BigDecimal.ONE, null,
                ItemStatus.GOOD);

        assertEquals("fr-1", out.getFridgeId());
        assertEquals("Yogur sin catálogo", out.getProductLabel());
        assertEquals(ItemStatus.GOOD, out.getStatus());
    }

    @Test
    void addLabeledItemToFridge_throwsWhenLabelBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addLabeledItemToFridge("fr-1", "  ", BigDecimal.ONE, null, ItemStatus.GOOD));
    }

    @Test
    void consumePartially_reducesQuantity() {
        FridgeItem half = new FridgeItem();
        half.setQuantity(BigDecimal.TEN);
        when(repository.findById("fi-1")).thenReturn(Optional.of(half));
        when(repository.save(any(FridgeItem.class))).thenAnswer(inv -> inv.getArgument(0));

        FridgeItem after = service.consumePartially("fi-1", new BigDecimal("5"));
        assertEquals(0, new BigDecimal("5").compareTo(after.getQuantity()));
    }

    @Test
    void consumePartially_marksConsumedWhenQuantityReachesZero() {
        FridgeItem one = new FridgeItem();
        one.setQuantity(BigDecimal.ONE);
        when(repository.findById("fi-2")).thenReturn(Optional.of(one));

        FridgeItem consumed = service.consumePartially("fi-2", BigDecimal.ONE);
        assertEquals(ItemStatus.CONSUMED, consumed.getStatus());
        assertEquals(0, BigDecimal.ZERO.compareTo(consumed.getQuantity()));
        verify(repository).deleteById("fi-2");
    }

    @Test
    void consumePartially_throwsWhenTooMuch() {
        when(repository.findById("fi-1")).thenReturn(Optional.of(existing));
        assertThrows(IllegalArgumentException.class, () -> service.consumePartially("fi-1", new BigDecimal("11")));
    }

    @Test
    void markAsConsumed_deletesItemFromInventory() {
        when(repository.findById("fi-1")).thenReturn(Optional.of(existing));

        service.markAsConsumed("fi-1");

        verify(repository).deleteById("fi-1");
    }

    @Test
    void getExpiringItems_and_getInventoryByCategory() {
        LocalDate d = LocalDate.now().plusDays(3);
        when(repository.findExpiringItems("fr-1", d)).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.getExpiringItems("fr-1", 3));

        when(repository.findByFridgeIdAndCategoryId("fr-1", "cat-1")).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.getInventoryByCategory("fr-1", "cat-1"));
    }

    @Test
    void update_and_delete() {
        when(repository.findById("fi-1")).thenReturn(Optional.of(existing));
        FridgeItem patch = new FridgeItem();
        when(repository.save(patch)).thenReturn(patch);
        assertEquals(patch, service.update("fi-1", patch));

        when(repository.findById("fi-1")).thenReturn(Optional.of(existing));
        service.delete("fi-1");
        verify(repository).deleteById(eq("fi-1"));
    }
}
